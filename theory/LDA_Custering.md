## Latent Dirichlet Allocation (LDA)

1. LDA is an unsupervised generative probabilistic model for a collection of
documents. The basic idea is that documents are represented as mixtures of latent
topics, where each topic is characterized by a distribution over words. This can
be useful in cases when we want to extract topics from a large corpus of text data.
It’s a way of automatically discovering topics that these documents (or sequence of sentences) contain.

2. LDA can also be used to shrink the large corpus of text data to some keywords
(sequence of keywords - using N-gram), reducing the task of clustering or searching
these huge number of documents (may be huge in size too) to clustering or searching
the keywords (topics). Thus, helps in reducing the number of resources required for
searching. The topic learned by the model can be used to automatically tag new incoming text data.

### I) Idea behind the algorithm

1. Technically the model assumes that the topics are specified before any data
has been generated. LDA represents documents as mixtures of topics that spit out
words with certain probabilities.

2. Think of it as if you are trying to write an article, and you choose that it’s
going to be 30% about food and 70% about sports. Now to generate each word in the document :-
    1. First picking a topic (according to the multinomial distribution that you
      sampled above; for example, you might pick the food topic with 3/10 probability
      and the sports topic with 7/10 probability).
    2. Using the topic to generate the word itself (according to the topic’s
      multinomial distribution). For example, if we selected the food topic, we
      might generate the word “broccoli” with 30% probability, “bananas” with 15%
      probability, and so on.

3. Assuming this generative model for a collection of documents and given the
documents, LDA then tries to backtrack from the documents to find a set of
topics that are likely to have generated the collection.

4. There are two important parameters
    1. Topic concentration / Beta
    2. Document concentration / Alpha

**For the symmetric distribution, a high alpha-value means that each document is
likely to contain a mixture of most of the topics, and not any single topic
specifically. A low alpha value puts less such constraints on documents and means
that it is more likely that a document may contain mixture of just a few, or even
only one, of the topics. Likewise, a high beta-value means that each topic is
likely to contain a mixture of most of the words, and not any word specifically,
while a low value means that a topic may contain a mixture of just a few of the
words.**

**If, on the other hand, the distribution is asymmetric, a high alpha-value means
that a specific topic distribution (depending on the base measure) is more likely
for each document. Similarly, high beta-values means each topic is more likely to
contain a specific word mix defined by the base measure.**

**In practice, a high alpha-value will lead to documents being more similar in
terms of what topics they contain. A high beta-value will similarly lead to
topics being more similar in terms of what words they contain.**

### II) Example

_Sample documents are:_

* I like to eat broccoli and bananas.
* I ate a banana and spinach smoothie for breakfast.
* Chinchillas and kittens are cute.
* My sister adopted a kitten yesterday.
* Look at this cute hamster munching on a piece of broccoli.

_Suppose we choose `k=2` (number of topics are 2) for our model, it gives us:_

* Topic A: 30% broccoli, 15% bananas, 10% breakfast, 10% munching, … (at which point, you could interpret topic A to be about food)
* Topic B: 20% chinchillas, 20% kittens, 20% cute, 15% hamster, … (at which point, you could interpret topic B to be about cute animals)

Now some new document can be tagged with the above given topics using the observations made by the LDA model.

1. Banana and spinach smoothie is a good combination for a healthy breakfast.
2. Kittens look cute as they munch on a bowl of milk, bananas, and chocolates.

Here, we can say that sentence 1 is 100% Topic A and sentence 2 is 40% Topic B with 60% Topic A.

### III) Overview of the algorithm

Suppose we have a collection of documents and we want to learn K topics out of them.

**Assumption: We are assuming that all topic assignments except for the current word in question are correct, and then updating the assignment of the current word using our model of how documents are generated.**

1. We will go through each document (d).
2. Then for each word (w) in the document we will calculate the following:-
    * X = p(topic | document) = the proportion of words in document d that are currently assigned to topic t.
    * Y = p(word w | topic t) = the proportion of assignments to topic t over all documents that come from this word w. (The same word can be in multiple documents, hence its coverage over the documents)
3. Then X * Y, according to our generative model, this is essentially the probability that topic t generated word w, so it makes sense that we resample the current word’s topic with this probability

_After repeating these steps for large enough number of times, we will get pretty good topic assignments such that, they generate words describing the documents._

### IV) Key points for data processing

***1) General Operators Required:***
1. Tokenizer or RegexTokenizer (for more powerful tokenize operation)
2. StopWordsRemover
3. N-Gram sequence of tokens
4. CountVectorizer
5. Regex operations to extract from raw text data, sequence of regex operations.
6. Lemmatization from "JohnSnowLabs" or "StanfordCoreNLP" NLP library (If using python then [spaCy](https://github.com/explosion/spaCy) can also be used).

***2) LDA Models Params (Supported in Apache Spark v2.3.0):***
1. Topic concentration / Beta
2. Document concentration / Alpha
3. K = number of topics we are expecting
4. Optimizer - "em" or "online" only these two are supported.

### V) Sample outputs

Dataset used [UCI Health-Tweet](https://archive.ics.uci.edu/ml/datasets/Health+News+in+Twitter)

_1)_
**N-Gram = 3, k = 10, Min document occurrence of key words = 1**

| topic | termIndices    | key_words                                                                            |
|-------|----------------|--------------------------------------------------------------------------------------|
| 0     | [9, 40, 37]    | [new study finds, pays new study, 10000 paying donors]                               |
| 1     | [9, 13, 95]    | [new study finds, type 2 diabetes, new study says]                                   |
| 2     | [13, 23, 92]   | [type 2 diabetes, breast cancer risk, high blood pressure]                           |
| 3     | [168, 5, 7]    | [2015 bestdiets rankings, pharmalot pharmalot pharmalittle, via wsj rt]              |
| 4     | [17, 93, 94]   | [healthtalk rt eatsmartbd, health daily digest, everyday health daily]               |
| 5     | [4, 5, 7]      | [rt pharmalot pharmalot, pharmalot pharmalot pharmalittle, via wsj rt]               |
| 6     | [87, 119, 124] | [case missed yesterday, five year forward, year forward view]                        |
| 7     | [0, 6, 8]      | [rt cynthiasass goodhealth, talknutrition rt cynthiasass, cynthiasass goodhealth q3] |
| 8     | 3, 274, 400]   | [todays getfit tip, rt allergyreliefny cnnhealth, chat 423 130]                      |
| 9     | [101, 120, 99] | [reports todays cartoon, reports todays headlines, health insurance exchanges]       |

_2)_
**N-Gram = 3, k = 10, Min document occurrence of key words = 2**

| topic | termIndices      | key_words                                                          |
|-------|------------------|--------------------------------------------------------------------|
| 0     | [3847, 493, 510] | [may lead lower, imports due bird, cancer study says]              |
| 1     | [20, 1434, 1121] | [ebola vaccine trial, mental health disorders, us poultry imports] |
| 2     | [8, 33, 16]      | [new health care, best worst foods, new years resolutions]         |
| 3     | [20, 47, 7539]   | [ebola vaccine trial, h5n1 bird flu, safety alert superbug]        |
| 4     | [105, 31, 28]    | [child mental health, mental health services, sierra leone ebola]  |
| 5     | [0, 9, 16]       | [todays getfit tip, help lose weight, new years resolutions]       |
| 6     | [1, 3, 7]        | [new study finds, affordable care act, work smoothly end]          |
| 7     | [47, 43, 28]     | [h5n1 bird flu, west africa ebola, sierra leone ebola]             |
| 8     | [28, 17, 138]    | [sierra leone ebola, ebola death toll, suspected ebola case]       |
| 9     | [17, 28, 43]     | [ebola death toll, sierra leone ebola, west africa ebola]          |

_3)_
**N-Gram = 3, k = 16, Min document occurrence of key words = 2**

| topic | termIndices        | key_words                                                              |
|-------|--------------------|------------------------------------------------------------------------|
| 0     | [3847, 3016, 300]  | [may lead lower, care study us, lose 10 pounds]                        |
| 1     | [4583, 4937, 1434] | [even little drinking, tackle ebola outbreak, mental health disorders] |
| 2     | [151, 138, 156]    | [sierra leone liberia, suspected ebola case, bird flu found]           |
| 3     | [7539, 4860, 8988] | [safety alert superbug, office workers back, patient evaluated ebola]  |
| 4     | [105, 31, 72]      | [child mental health, mental health services, tackle mental health]    |
| 5     | [16, 9, 33]        | [new years resolutions, help lose weight, best worst foods]            |
| 6     | [1, 5, 7]          | [new study finds, smoothly end november, work smoothly end]            |
| 7     | [47, 43, 151]      | [h5n1 bird flu, west africa ebola, sierra leone liberia]               |
| 8     | [19, 138, 28]      | [hepatitis c drug, suspected ebola case, sierra leone ebola]           |
| 9     | [17, 28, 47]       | [ebola death toll, sierra leone ebola, h5n1 bird flu]                  |
| 10    | [8, 16, 15]        | [new health care, new years resolutions, health care law]              |
| 11    | [16, 9, 99]        | [new years resolutions, help lose weight, sweepstakes chance win]      |
| 12    | [493, 74, 190]     | [imports due bird, birth control pills, dies h5n1 bird]                |
| 13    | [9, 16, 147]       | [help lose weight, new years resolutions, wed love share]              |
| 14    | [2884, 8984, 7413] | [annuities next big, trackers play big, babies dna 3]                  |
| 15    | [0, 2, 9]          | [todays getfit tip, type 2 diabetes, help lose weight]                 |


### Some more theory links
1. In simple terms - [LDA Layman's guide](http://blog.echen.me/2011/08/22/introduction-to-latent-dirichlet-allocation/)
2. Basics of MLE - [Maximum Likelihood Estimation](https://towardsdatascience.com/probability-concepts-explained-maximum-likelihood-estimation-c7b4342fdbb1)
3. UMAS - [slides](http://people.cs.umass.edu/~wallach/talks/priors.pdf)
4. Detail Maths and logic - [Safari Books - Data Clustering](https://www.safaribooksonline.com/library/view/data-clustering/9781466558229/)

 _Note: Point 3 source is good for those who have prior knowledge of advanced maths concepts._
