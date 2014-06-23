# vagrant up
```vagrant up``` is all you need to know to start the [batcher's project](https://github.com/cegeka/batchers). Vagrant is not a new project and it is quite popular. There are lots of blog posts about it so I will add a very short description. You may skip it if you are familiar and jump right to the advanced multi machine setup, the networking and packaging of boxes.

# Vagrant Short intro
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

# The Vagrantfile
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

# Multi machine setup
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

Wee needed for our configuration that the two machines see each other. By default vagrant machines have the same IP and can't ping each other. By manually setting private network ip to the master we were able to connect the slave to the master.

But I want my slave to connect to some other master. How do I do that? Well it's simple: we use environment variables. Jus run:

```BATCHERS_MASTER_IP=192.168.1.100 vagrant up slave```

Where ```192.168.1.100``` is your master's IP address.

## Packaging boxes
Setting up a master and a slave machine taxes up quite some time. Fortunately Vagrant offers a option to package these boxes after they have been built. Packaged boxes can be shared over a FTP server and then the deployment to other machines is basically a FTP copy that is done in the background by Vagrant. 

If you want to package Vagrant boxes you can check out the script that packages these boxes in the [vagrant_package.sh](https://github.com/cegeka/batchers/blob/master/taxcalculator-vagrant/vagrant_package.sh) file.

Pulling our pre-built images is simple: just add ```box``` in front of your machine's name:

```
vagrant up boxmaster
vagrant up boxslave
vagrant up boxstandalone
```