# vagrant up
```vagrant up``` is all you need to know to start the [batcher's project](https://github.com/cegeka/batchers). Vagrant is not a new project and it is quite popular. There are lots of blog posts about it so I will add a very short description. You may skip it if you are familiar and jump right to the advanced multi machine setup, the networking and packaging of boxes.

## Vagrant Short intro
Setting up complex projects for several developers is quite complex. For our project we need Oracle JDK8, Maven, MySQL and two Tomcat instances. This is quite time consuming if you want to install everything by clicking "Next" buttons. Vagrant provides a easy way to share complex environments inside virtual machines. It can use several virtualization "providers". We used VirtualBox since it is free and open source. If you want to try it out just install [VirtualBox](https://www.virtualbox.org/) and [Vagrant](http://www.vagrantup.com/) and run these commands:

```sh
git clone https://github.com/cegeka/batchers.git
cd batchers/taxcalculator-vagrant
vagrant up
```

Now visit [localhost:9090/taxcalculator](http://localhost:9090/taxcalculator/).

This will grab a bare bone virtual machine with Ubuntu 14.04 from [vagrantbox.es](http://www.vagrantbox.es/) and it will start running provisioning scripts. You can ssh into the machine with:
```sh
vagrant ssh
```

## The Vagrantfile
In our project there is a file called [Vagrantfile](https://github.com/cegeka/batchers/blob/master/taxcalculator-vagrant/Vagrantfile). It is a Ruby based [DSL](http://en.wikipedia.org/wiki/Domain-specific_language) and it defines the way we configure the machines. Let's split this file into simple pieces:

```ruby
  config.vm.box_url = "https://somehost...../ubuntu-14.04.box"
  config.vm.provider "virtualbox" do |v|
    v.memory = 1024
  end
```
This part defines the base Ubuntu image location that will be used to build the machines and the RAM of the machines when using Virtualbox as a virtualization provider.

Vagrant uses several types of [provisioning](https://docs.vagrantup.com/v2/provisioning/index.html) which start to install and configure the machine. The __file__ provisioner uploads a files or folders from the local machine to the virtual machine while __shell__ runs a script inside the virtual machine.

```ruby
  config.vm.provision "file", source: "scripts", destination: "scripts"
  config.vm.provision "shell", path: "provision.sh"
```

## Multi machine setup
Our setup contains at least a master/slave configuration. The __vagrant up__ command sets up just one machine with no master/slave. In order to create separate machines we need to use syntax that looks like this ```my_machine_config.vm.my_property```. Everything that starts with ```config.vm.my_property``` configures all the machines. Here is a short description on how we configured the master/slave:

```ruby
  config.vm.define "slave" do |slave|
    slave.vm.network "private_network", ip: "192.168.50.3"
  end
  config.vm.define "master" do |master|
    master.vm.network "private_network", ip: "192.168.50.4"
  end

```

In order to run the master and slave machines just run either ```vagrant up master```, ```vagrant up slave``` or both ```vagrant up master slave```. SSH-in into the machines is similar: ```vagrant ssh master``` or ```vagrant ssh slave```.

We needed for our configuration that the two machines see each other. By default vagrant machines have the same IP and can't ping each other. By manually setting private network ip to the master we were able to connect the slave to the master.

But I want my slave to connect to some other master. How do I do that? Well it's simple: we use environment variables. Jus run:

```BATCHERS_MASTER_IP=192.168.1.100 vagrant up slave```

Where ```192.168.1.100``` is your master's IP address.

## Packaging boxes and faster "vagrant up"
Setting up a master and a slave machine taxes up quite some time. Fortunately Vagrant offers a option to package these boxes after they have been built. Packaged boxes can be shared over a FTP server and then the deployment to other machines is basically a FTP copy that is done in the background by Vagrant. 

If you want to package Vagrant boxes you can check out the script that packages these boxes in the [vagrant_package.sh](https://github.com/cegeka/batchers/blob/master/taxcalculator-vagrant/vagrant_package.sh) file.

Pulling our pre-built images is simple: just add ```box``` in front of your machine's name:

```
vagrant up boxmaster
vagrant up boxslave
vagrant up boxstandalone
```

Vagrant is a great tool and it helps you spending less time on configuring complex setups and more on focusing on your app. It proved quite useful for us in the [batchers](https://github.com/cegeka/batchers) project.

# Docker
[Docker](http://www.docker.com) is the new kid on the bloc when it comes to deploying applications. It glues together various features of the Linux kernel (containers, cgroups, aufs, btrfs) and creates a simple yet lightwait way to deploy apps in isolated and secure environments. As oposet to Vagrant, Docker is meant for deploying in production, not just sharing the same development environment with other developers.

## Batchers slaves with Docker
For the Batchers project we used it to deploy slaves. In order to do this you need to first build the container image and then run it. Building the container image is done only once on each machine. If you have a docker private [registry](https://github.com/dotcloud/docker-registry#docker-registry), you build it one on one machine and use it for all your machines. If you do not have a private registry you just need to run two commands in order to run a registry.

```
git clone https://github.com/cegeka/batchers.git
cd batchers/taxcalculator-vagrant
# run once
./docker_build_batchersslave.sh
# run for each new slave instance
./docker_run_batchersslave.sh
```

To check the list of containers run ```docker ps```.

# Batchers/Docker Limitations
## Same LAN
The first and most notable limitation is the network limitation. This is strictly related to the Batchers project not to Docker itself. The Batchers master and the Docker Batchers slaves need to be on the same LAN. The Docker support for our app was only as a sample and our goal was not to make it complex. The script for running a slave ```docker_run_batchersslave.sh``` will fetch your IP address of the eth0 interface and send it as a environment variable to the container. If you do not use a GNU/Linux operating system copy and paste the ``docker run....``` line in the script and replace the IP address with your own. If you need Docker complex networking setups check out [Pipework](https://github.com/jpetazzo/pipework#pipework-software-defined-networking-for-linux-containers)

## Containers are to thin
The Docker developers recommend using one process per container and link containers between them with [Container Linking](https://docs.docker.com/userguide/dockerlinks/#docker-container-linking) and the [Ambassador Pattern](https://docs.docker.com/articles/ambassador_pattern_linking/). So I will run only Tomcat inside my slave container, right? Well... how will I SSH into it then or how will I check out the logs of syslog? Ok, I will and both Tomcat and OpenSSH server. But now I need an init system. Well the nice people at [Phusion](http://www.phusion.nl/) don't like the [single process per container](https://github.com/phusion/baseimage-docker#wait-i-thought-docker-is-about-running-a-single-process-in-a-container) paradigm and they beefed up a Ubuntu base image with [lots of features](https://github.com/phusion/baseimage-docker#whats-inside-the-image) including a light built-for-docker init system. We used baseimage-docker and we recommend it to anybody starting to use Docker. It makes Docker feel less strange until you get used to "the Docker way".

## The /etc/hosts hack
Ok, this one is a biggie. Apparently due to the fact that Docker containers share a lot of their file system in a read-only mode you cannot write /etc/hosts. Wee need that since that's how the slaves connect to the master database. There is an [issue](https://github.com/dotcloud/docker/issues/2267) for this problem but it is not yet fixed. So we added a [ugly hack](https://github.com/cegeka/batchers/blob/master/taxcalculator-vagrant/Dockerfile#L23-L26) to make this work for us.

## Complicated command parameters
Another problem with Docker is the fact that even if you can do almost anything with its command line tools, sometimes you need lots of parameters and things get ugly fast. This command runs a new slave container ```docker run -i -t -d -e "BATCHERS_MASTER_IP=172.17.42.1" batchersslave``` and if you need to see the IP of the last started container in order to SSH into it you only need to run this really simple command ```docker inspect `docker ps -q`|grep IPAddress```. Simple right? Well... not quite. If you want to see the list of running containers and their IP addresses things get really complicated. 

That's why I started writing some simple functions in order to make working with Docker less complicated. You can check them out [here](https://github.com/andreicristianpetcu/dotfiles/blob/master/my-aliases.sh#L218-L277). I'm using [oh-my-zsh](http://ohmyz.sh/) for managing my [dotfiles](http://dotfiles.github.io/).

My workflow is simple:

 - spin a new container
```
./docker_run_batchersslave.sh
```
 - check the list of containers:
```
$dockerlist 
IP Address      Container ID    Image ID         Name
172.17.0.4     a119f7a6d242    batchersslave     /mad_turing
172.17.0.3     42f808c08864    batchersslave     /hopeful_nobel
172.17.0.2     f6448ae6d29e    batchersslave     /tender_poincare
Total containers 3

```
 - ssh into one of them (I'm using the public insecure private key in ~/.insecure_key for my dev environment)
```
$sshiinsecurekeyroot 172.17.0.4
root@a119f7a6d242:~#
```

Feel free to fork [my dotfiles](https://github.com/andreicristianpetcu/dotfiles) or write your won functions for managing Docker, just don't be intimidated by the long parameters that Docker requires.


## Docker final verdict
Ok so there are some limitations with working with Docker but still it is an excellent tool, it has lots of startups that base their work on Docker and the big companies have been using it since before it was considered "stable" by it's developers. We really like it and we will probably use it in the future.