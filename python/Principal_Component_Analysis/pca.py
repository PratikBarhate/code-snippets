import pandas
import numpy
from sklearn.preprocessing import StandardScaler

# load data
# k = number of features after dimensionality reduction
# change the file path as required
iris_df = pandas.read_csv(
    filepath_or_buffer="../dataset/IrisData.csv",
    header=None, sep=",")
iris_df.columns = ['sepal_len', 'sepal_wid', 'petal_len', 'petal_wid', 'label']

# drop the empty lines (rows)
iris_df.dropna(how="all", inplace=True)

# Separate the features and label (class)
# We do not require label for PCA
X = iris_df.iloc[:, 0:4].values
# columns are inclusive:exclusive, hence to return a Dataframe last_col:one_plus_max_index
Y = iris_df.iloc[:, 4:5].values

# Scale the features so that all features are having equal importance
X_std = StandardScaler().fit_transform(X)

# compute co-variance matrix
cov_mat = numpy.cov(X_std.T)

# computing eigen values and eigen vectors, using eigen value decomposition
# in eig_vecs column represents a vector
eig_vals, eig_vecs = numpy.linalg.eig(cov_mat)
print("\n* Using Eigen Value Decomposition\nEigen Values -> {0}\nEigen Vectors ::\n{1}".format(eig_vals, eig_vecs))

# computing eigen values and eigen vectors, using singular value decomposition
# If the data matrix is centered to have zero mean then EVD and the SVD are exactly the same.
# using eigen value decomposition is a better way, if you cannot verify the mean is tending to zero
u, s, v = numpy.linalg.svd(cov_mat)
print("\n* Using Singular Value Decomposition\nEigen Values -> {0}\nEigen Vectors ::\n{1}".format(s, v))

# computing correlation matrix, used more often in financial fields
cor_mat = numpy.corrcoef(X_std.T)
eig_vals_cor, eig_vecs_cor = numpy.linalg.eig(cor_mat)
print("\n* Using correlation matrix (EVD)\nEigen Values -> {0}\nEigen Vectors ::\n{1}".
      format(eig_vals_cor, eig_vecs_cor))

# Make a list of (eigen-value, eigen-vector) tuples
# sort by eigen-values (first element in the tuple), in descending order
# vectors are converted to shape(1, 4)
eig_pairs = [(numpy.abs(eig_vals_cor[i]), eig_vecs_cor[:, i]) for i in range(len(eig_vals_cor))]
eig_pairs.sort(key=lambda x: x[0], reverse=True)
print("\n* Making sure visually eigen values are arrange in descending order")
for i in eig_pairs:
    print(i[0])

# deciding which (how many) top features or vectors to select so as there is minimum loss of information
sum_eign_vals = numpy.sum(eig_vals, axis=0)
# selecting vectors which contain 95% or more of the information
total = 0.0
k = 0
# cp = cumulative percent
cp = 0.0
while k < eig_vals.size and cp < 95.0:
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
X_red = X_std.dot(w)
print("\n* Transformed Data ::\n{0}".format(X_red))
