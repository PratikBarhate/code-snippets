#### Scala - Code to search in a large text file using akka-actors to sort parts of the file parallely and generate meta-data, used to search the file.(meta-data has to generated again if the file is updated)


### Compile source code

*Pre-requisites :: maven - v3.3.9 or higher and java - v1.8 or higher*

1. Clone the repository
2. Go to the cloned diretory and run command `cd scala/textsearch/SplitSortAndSearch/`
3. Build the code with `mvn install`, for re-building `mvn clean install`
4. A directory name `target` will be created
5. Run command `cd target`
6. Inside the 'target' directory `SplitSortAndSearch-1.0-scala.tar.gz` file will
be created

### Execution

1. Run command `tar xvf SplitSortAndSearch-1.0-scala.tar.gz`
2. Run command `cd SplitSortAndSearch-1.0`
3. To search in a file you need to first create the sorted structure, for that
run command `./bin/sort.sh file_path`
4. To search a word in that file run `./bin/search.sh file_path word_you_want_to_search`.
5. Inside the directory `SplitSortAndSearch-1.0/etc` there is file called
`application.conf` which specifies the number of splits(partitions) to be created
while sorting the file. Deafult is set to `N = 10`

**File paths cannot have directory names which have spaces in them**

*If you want the intermediate sorted files of each partition comment the last
line of the bash script `sort.sh` and then you have to re-compile the source*
