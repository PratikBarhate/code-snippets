### code-snippets
Short machine learning code snippets - python, spark(scala)

_Some basic theory links and naive notes are in the directory_ [theory](https://github.com/Pratik-Barhate/code-snippets/tree/master/theory)

#### python dependencies
1. python - v3.6.3
2. pandas - v0.21.0
3. numpy - v1.13.3
4. sklearn - v0.19.1

#### scala dependencies
1. JVM - 1.8+ (Java 8 or above)
2. Maven - v3.5.2 (All other dependencies are already added in the pom, hence maven handles rest.)
3. You need to install Apache Spark - v2.3.0 locally if you want to execute using `spark-submit` command.

### Sample codes in python

| Sr.No. | Algorithm                             | Type                     | Links                                              |
|--------|---------------------------------------|--------------------------|----------------------------------------------------|
| 1      | PCA (Principal Component Analysis)    | Dimensionality Reduction | [Code](https://github.com/Pratik-Barhate/code-snippets/blob/master/python/Principal_Component_Analysis/pca.py) ::  [Theory](https://github.com/Pratik-Barhate/code-snippets/blob/master/theory/PCA.md) |
| 2      | LDA (Linear Discriminant Analysis)    | Dimensionality Reduction | [Code](https://github.com/Pratik-Barhate/code-snippets/blob/master/python/Linear_Discriminant_Analysis/lda.py) ::  [Theory](https://github.com/Pratik-Barhate/code-snippets/blob/master/theory/LDA_Dimension_Reduction.md) |

### Sample codes in scala (spark)

| Sr.No. | Algorithm                             | Type                     | Links                                              |
|--------|---------------------------------------|--------------------------|----------------------------------------------------|
| 1      | Logistic Regression                   | Classification           | [Code](https://github.com/PratikBarhate/code-snippets/blob/master/scala/spark/src/main/scala/io/github/pratikbarhate/spark/ml/LogisticReg.scala) :: [Theory](https://github.com/ujjwalkarn/Machine-Learning-Tutorials#topic)(Find Logistic Regression section) |
| 2      | Latent Dirichlet Allocation           | Document clustering (topic modeling) | [Code](https://github.com/PratikBarhate/code-snippets/blob/master/scala/spark/src/main/scala/io/github/pratikbarhate/spark/ml/LDADocClustering.scala) ::   [Theory](https://github.com/Pratik-Barhate/code-snippets/blob/master/theory/LDA_Custering.md) |
| 3      | CollectMap                            | UDAF for Apache Spark    | [Code](https://github.com/PratikBarhate/code-snippets/blob/master/scala/spark/src/main/scala/io/github/pratikbarhate/spark/udafs/CollectMap.scala) |
| 4      | Median (simple)                       | UDAF for Apache Spark    | [Code](https://github.com/PratikBarhate/code-snippets/blob/master/scala/spark/src/main/scala/io/github/pratikbarhate/spark/udafs/SimpleMedian.scala) |

### Executing the sample codes
#### Scala
_Scala codes can be executed through IDEs by running the main method of the specific class._

_To execute the spark related task using `spark-submit` command_ :-
1. Go to the scala project directory `cd ${project_root_dir}/scala`.
2. Execute `mvn clean install`.
3. To try some specific class execute `spark-submit --class spark.${class_name} target/spark-0.1.jar`.
For this you need spark locally installed. If facing any issues in bind address try
`spark-submit --class spark.${class_name} --conf spark.driver.bindAddress=127.0.0.1 target/spark-0.1.jar`

#### Python
1. Using IDEs run the given file.
2. Using terminal execute command `python file_name.py` or `python3 file_name.py`
depending on where the related dependencies are installed.

### License
[MIT](https://github.com/Pratik-Barhate/code-snippets/blob/master/LICENSE)
