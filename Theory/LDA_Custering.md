## Latent Dirichlet Allocation (LDA)

_It is a generative probabilistic model of a collection of documents. The basic idea is that documents are represented as mixtures of latent topics, where each topic is characterized by a distribution over words. This can be useful in cases,
when we want to extract topics from large corpus of text data._

### Theory links
1. In simple terms - [LDA Layman's guide](http://blog.echen.me/2011/08/22/introduction-to-latent-dirichlet-allocation/)
2. Basics of MLE - [Maximum Likelihood Estimation](https://towardsdatascience.com/probability-concepts-explained-maximum-likelihood-estimation-c7b4342fdbb1)
3. UMAS - [slides](http://people.cs.umass.edu/~wallach/talks/priors.pdf)
4. Detail Maths and logic - [Safari Books - Data Clustering](https://www.safaribooksonline.com/library/view/data-clustering/9781466558229/)

 _Note: Point 3 source is good for those who have prior knowledge of advanced maths concepts._

### Key Points

***I) General Operators Required:***
1. Tokenizer or RegexTokenizer (for more powerful tokenize operation)
2. StopWordsRemover
3. N-Gram sequence of tokens
4. CountVectorizer
5. Regex operations to extract from raw text data, sequence of regex operations.
6. Lemmatization from "JohnSnowLabs" or "StanfordCoreNLP" NLP library (If using python then [spaCy](https://github.com/explosion/spaCy) can also be used).

***II) LDA Models Params (Supported in Apache Spark v2.3.0):***
1. Topic concentration / Beta
2. Document concentration / Alpha
3. K = number of topics we are expecting
4. Optimizer - "em" or "online" only these two are supported.

***III) Intuition on the values of Alpha and Beta:***
For the symmetric distribution, a high alpha-value means that each document is likely to
contain a mixture of most of the topics, and not any single topic specifically.
A low alpha value puts less such constraints on documents and means that it is more likely
that a document may contain mixture of just a few, or even only one, of the topics.
Likewise, a high beta-value means that each topic is likely to contain a mixture of most of the words,
and not any word specifically, while a low value means that a topic may contain a mixture of just
a few of the words.

If, on the other hand, the distribution is asymmetric, a high alpha-value means that a specific
topic distribution (depending on the base measure) is more likely for each document.
Similarly, high beta-values means each topic is more likely to contain a specific
word mix defined by the base measure.

In practice, a high alpha-value will lead to documents being more similar in terms
of what topics they contain. A high beta-value will similarly lead to topics being
more similar in terms of what words they contain.

### Sample outputs

Dataset used [UCI Health-Tweet](https://archive.ics.uci.edu/ml/datasets/Health+News+in+Twitter)

_I)_
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

_II)_
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

_III)_
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
