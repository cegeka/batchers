#Performance results

##Table of contents
* Setup
* Scenarios
* Hardware

###Setup
For all the tests we used the same amount of data to be processed - __1000 employees__

###Scenario's

Below you can find all the scenario's we tested. In all scenario's, the database was running on a different machine.

#### Single JVM, single threaded

Here we run the taxcalculator application on one single JVM with a SyncTaskExecutor. This means all items in a
certain step are processed synchronously.

####Single JVM, multi threaded

Here we run the taxcalculator application on one single JVM with a SimpleAsyncTaskExecutor. This means all items
in a certain step are processed in parallel.

####Multi JVM, single threaded

Here we run the taxcalculator application on multiple JVM using a master slave configuration. The tasks are
distributed using partitioning and each slave is using a SyncTaskExecutor. This means all items on a slave are
processed synchronously.

Here, we do the performance measurements once with 2 slaves and once with 4 slaves.

####Multi JVM, multi threaded

Here we run the taxcalculator application on multiple JVM using a master slave configuration. The tasks are
distributed using partitioning and each slave is using a SimpleAsyncTaskExecutor. This means all items on a
slave are processed in parallel.

Here, we do the performance measurements once with 2 slaves and once with 4 slaves.

###Performance results
    
|Scenario                               | Running time |
|---------------------------------------|-------------:|        
| Single JVM single threaded            | 18:00        |
| Single JVM, multi threaded            | 5:00         |
| Multi JVM, single threaded - 2 slaves | 10:00        |
| Multi JVM, single threaded - 4 slaves | 5:30         |        
| Multi JVM, multi threaded - 2 slaves  | 3:00         |
| Multi JVM, multi threaded - 4 slaves  | TODO:        |
        
###Hardware
|Machine Name    | Installed OS      |Processor                                                 | Memory |
|----------------|-------------------|----------------------------------------------------------|--------|
|Database server | Windows 7, 64-bit |Intel Core i7-3740QM Processor (6M Cache, up to 3.70 GHz) | 8 GB   |         
|Master          | Windows 7, 64bit  |Intel Core i7 3770 3.4GHz (4 cores, 8 threads)            | 16 GB  |
|Slave1          | Windows 7, 64bit  |Intel Core i7 3770 3.4GHz (4 cores, 8 threads)            | 16 GB  |
|Slave2          | Windows 7, 64bit  |Intel Core i7 3770 3.4GHz (4 cores, 8 threads)            | 16 GB  |
|Slave3          | Windows 7, 64bit  |Intel Core i7 3770 3.4GHz (4 cores, 8 threads)            | 16 GB  |
|Slave4          | Windows 7, 64bit  |Intel Core i7 3770 3.4GHz (4 cores, 8 threads)            | 16 GB  | 