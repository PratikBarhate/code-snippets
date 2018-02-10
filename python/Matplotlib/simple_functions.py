import matplotlib.pyplot as plt
import numpy as np

# Prepare the data
# two lines to be plotted
x = np.linspace(-2, 2, 100)

# Plot the data
plt.plot(x, x, label="linear")
plt.plot(x, x**3, label="cubic")
plt.plot(x, x**2, label="quadratic")

# Add a legend
# This is like the key to identify different elements or observations in one graph,
# here we have 'first' and 'second' as the elements or observations
plt.legend()

# Show the plot
plt.show()


# plot a thick with few data points with marker
fig = plt.figure()
ax = fig.add_subplot(111)
ax.plot([1, 2, 3, 4], [10, 20, 25, 30], color='lightblue', linewidth=3)
ax.scatter([0.3, 3.8, 1.2, 2.5], [11, 25, 9, 26], color='darkgreen', marker='^')
ax.set_xlim(0.5, 4.5)
plt.show()

# change the style (background) for better visualization
plt.style.use("ggplot")

plt.plot([1, 2, 3, 4], [10, 20, 25, 30], color='lightblue', linewidth=3)
plt.scatter([0.3, 3.8, 1.2, 2.5], [11, 25, 9, 26], color='darkgreen', marker='^')
plt.xlim(0.5, 4.5)
plt.show()
