### Principal-Component-Analysis (PCA)

1. PCA helps in identifying and finding patterns to reduce the dimensions of the dataset with minimal loss of information.
2. Sites to understand PCA:
    1. With Iris [data](https://plot.ly/ipython-notebooks/principal-component-analysis/)
    2. With Random [data](http://sebastianraschka.com/Articles/2014_pca_step_by_step.html#what-is-a-good-subspace)
3. Ways to compute eigen values, eigen vectors and its use in PCA - [Eigen Value Decomposition](https://www.cc.gatech.edu/~dellaert/ftp/svd-note.pdf)

#### Libraries and Python version

1. python - v3.6.3
2. pandas - v0.21.0
3. numpy - v1.13.3
4. sklearn - v0.19.1

#### NOTE

1. If the data matrix is centered to have zero mean then EVD and the SVD are exactly the same.
Using eigen value decomposition is a better way, if you cannot verify the mean is tending to zero
2. Standardizing the features is good practice, though it won't affect the eigen values, eigen vectors will be different
hence a different new subspace, standardized new subspace will be good for further machine learning tasks, especially if 
the features are measured on different scales.
3. Deciding the value of `k` = Number of features in the new sub space can be done as :-
    1. Select the P percent of information to be sustained.
    2. Calculate cumulative % of information from top to bottom eigen values.
    3. If the cumulative % is greater than or equal to P exit or else go to step 2.
4. Select the eigen vectors of the select top 'k' eigen values.
5. Using these selected vectors transform the original data to the desired new sub space.    