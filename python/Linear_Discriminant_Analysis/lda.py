import pandas
import numpy
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import StandardScaler

# load data
# change the file path as required
iris_df = pandas.read_csv(
    filepath_or_buffer="/Users/tkmah7q/Pratik/Study/Projects/ml-snippets/python/dataset/IrisData.csv",
    header=None, sep=",")
iris_df.columns = ["sepal_len", "sepal_wid", "petal_len", "petal_wid", "label"]

# drop the empty lines (rows)
iris_df.dropna(how="all", inplace=True)

# X - Data is standardized
X = StandardScaler().fit_transform(iris_df.iloc[:, 0:4].values)
y = iris_df["label"].values

# Set integer values for each separate class,
enc = LabelEncoder()
label_encoder = enc.fit(y)
y = label_encoder.transform(y) + 1

label_dict = {1: "Setosa", 2: "Versicolor", 3: "Virginica"}

print("\n* Classes: {0}".format(label_dict))

numpy.set_printoptions(precision=4)

# Calculating means for each classes
print("\n* Mean Vectors :-")
mean_vectors = []
for cl in range(1, 4):
    mean_vectors.append(numpy.mean(X[y == cl], axis=0))
    print("\n - class {0}: {1}".format(cl, mean_vectors[cl - 1]))

# Find within-class scatter matrix
# M = xi - mi, where 'x' is the each row and 'm' is mean of the given class 'i'
# S_W = SummationOf(Transpose(M).M) || . = Matrix multiplication
S_W = numpy.zeros((4, 4))
for cl, mv in zip(range(1, 4), mean_vectors):
    class_sc_mat = numpy.zeros((4, 4))  # scatter matrix for every class
    for row in X[y == cl]:
        row, mv = row.reshape(4, 1), mv.reshape(4, 1)  # make column vectors
        temp_mat = (row - mv)
        class_sc_mat += temp_mat.dot(temp_mat.T)
    S_W += class_sc_mat  # sum class scatter matrices
print("\n* Within-class Scatter Matrix:\n{0}".format(S_W))

# Now we need overall mean and between classes scatter matrix
# M = mi - om, where 'mi' is the row of mean for a class 'i', 'om' is the overall mean.
# N =  number of observations in the class 'i'.
# S_B = SummationOf(N * Transpose(M).M) || . = Matrix multiplication
overall_mean = numpy.mean(X, axis=0).reshape(4, 1)
S_B = numpy.zeros((4, 4))
for i, mean_vec in enumerate(mean_vectors):
    n = X[y == i + 1, :].shape[0]
    mean_vec = mean_vec.reshape(4, 1)
    temp_mat = mean_vec - overall_mean
    S_B += n * temp_mat.dot(temp_mat.T)
print("\n* Between-class Scatter Matrix:\n{0}".format(S_B))

# Now compute the eigen values and eigen vectors
# using numpy's eigen value decomposition (EVD) method
eig_vals, eig_vecs = numpy.linalg.eig(numpy.linalg.inv(S_W).dot(S_B))

# Make a list of (eigen-value, eigen-vector) tuples
# sort by eigen-values (first element in the tuple), in descending order
# vectors are converted to shape(1, 4)
eig_pairs = [(numpy.abs(eig_vals[i]), eig_vecs[:, i]) for i in range(len(eig_vals))]
eig_pairs.sort(key=lambda x: x[0], reverse=True)
print("\n* Making sure visually eigen values are arrange in descending order")
for i in eig_pairs:
    print(i[0])

# deciding which (how many) top features or vectors to select so as there is minimum loss of information
sum_eign_vals = numpy.sum(eig_vals, axis=0)
# selecting vectors which contain 99.95% or more of the information
total = 0.0
k = 0
# cp = cumulative percent
cp = 0.0
while k < eig_vals.size and cp < 99.95:
    total = total + eig_vals[k]
    cp = (total/sum_eign_vals) * 100
    print("\n* Cumulative percent for first {0} vector/s is {1}".format(k+1, cp))
    k = k + 1

# value of `k + 1` number of vectors are to be selected
# get the first k elements from `eig_pairs`
# We will get vectors in the shape(1, 4), we want to reshape to (4, 1) as we want a column to be a vector
sel_vecs = [eig_pairs[i][1].reshape(4, 1) for i in range(0, k)]
# w = all the required vectors stacked horizontally
w = numpy.hstack(tuple(sel_vecs))
print("\n* Vectors along which data has to reduced ::\n{0}".format(w))

# Sample data to new sub-space
# `X_red` is the transformed data of `Number of Rows x K`, K = new reduce space dimension
X_red = X.dot(w)
print("\n* Transformed Data ::\n{0}".format(X_red))
