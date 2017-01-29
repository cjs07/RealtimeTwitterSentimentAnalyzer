import csv
import nltk
import time
import pickle
import GraphMaker


# Grabs all the words from the tweets provided and aggregates them into a long list
def get_words_in_tweets(tweets):
    all_words = []
    for (words, sentiment) in tweets:
        all_words.extend(words)
    return all_words


# Calculates the frequency of each word appearing in tweets
def get_word_features(wordlist):
    wordlist = nltk.FreqDist(wordlist)
    word_features = wordlist.keys()
    return word_features


# Makes a dictionary out of the unique words seen in the tweets
def extract_features(document):
    document_words = set(document)
    features = {}
    for word in word_features:
        features['contains(%s)' % word] = (word in document_words)
    return features

# Used to grab the root of a word
s = nltk.stem.SnowballStemmer('english')

tweets = []

TRAINING_AMOUNT = 1000

start_time = time.clock()
print('Reading in tweets!')
with open('sentiment_analysis_dataset.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip headers
    next(plots)
    for i, row in enumerate(plots):
        # Kick us out of the loop if we get to line i of the input file.
        if i == TRAINING_AMOUNT:
            break
        # Break the tweet down into the individual stemmed words
        # Append the stemmed tweet as well as the sentiment to the list
        temp = s.stem(row[3]), row[1]
        tweets.append(temp)
print('Finished reading tweets!')
filtered_tweets = []
# Take all of the strings stored in the tweet lists, along with the sentiment labels
for (words, sentiment) in tweets:
    # Filter the set of words to include only those that have at least 4 characters and don't start with a hashtag
    words_filtered = [e.lower() for e in words.split() if len(e) >= 4 and not e.startswith('@')]
    filtered_tweets.append((words_filtered, sentiment))
print('Wrote all training data into a giant list!')

# Calculate how often we see each word in a positive or negative tweet
word_features = get_word_features(get_words_in_tweets(filtered_tweets))

sentiments = []
times = []
classifier_load = open('classifier.pickle', 'rb')
classifier = pickle.load(classifier_load)
classifier_load.close()
classifier.show_most_informative_features(50)
with open('sun_tweets.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip first line of file (headers)
    next(plots)
    for i, row in enumerate(plots):
        if i % 10 != 0:
            continue
        times.append(row[0])
        sentiments.append(int(classifier.classify(extract_features(row[3].split()))))
GraphMaker.make_graph(sentiments, times, 'Sunday, Jan 22')
print('Sunday graph complete.')

sentiments = []
times = []
with open('mon_tweets.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip first line of file (headers)
    next(plots)
    for i, row in enumerate(plots):
        if i % 10 != 0:
            continue
        times.append(row[0])
        sentiments.append(int(classifier.classify(extract_features(row[3].split()))))
GraphMaker.make_graph(sentiments, times, 'Monday, Jan 23')
print('Monday graph complete.')

sentiments = []
times = []
with open('tue_tweets.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip first line of file (headers)
    next(plots)
    for i, row in enumerate(plots):
        if i % 10 != 0:
            continue
        times.append(row[0])
        sentiments.append(int(classifier.classify(extract_features(row[3].split()))))
GraphMaker.make_graph(sentiments, times, 'Tuesday, Jan 24')
print('Tuesday graph complete.')

sentiments = []
times = []
with open('wed_tweets.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip first line of file (headers)
    next(plots)
    for i, row in enumerate(plots):
        if i % 10 != 0:
            continue
        times.append(row[0])
        sentiments.append(int(classifier.classify(extract_features(row[3].split()))))
GraphMaker.make_graph(sentiments, times, 'Wednesday, Jan 25')
print('Wednesday graph complete.')

sentiments = []
times = []
with open('thu_tweets.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip first line of file (headers)
    next(plots)
    for i, row in enumerate(plots):
        if i % 10 != 0:
            continue
        times.append(row[0])
        sentiments.append(int(classifier.classify(extract_features(row[3].split()))))
GraphMaker.make_graph(sentiments, times, 'Thursday, Jan 26')
print('Thursday graph complete.')

sentiments = []
times = []
with open('fri_tweets.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip first line of file (headers)
    next(plots)
    for i, row in enumerate(plots):
        if i % 10 != 0:
            continue
        times.append(row[0])
        sentiments.append(int(classifier.classify(extract_features(row[3].split()))))
GraphMaker.make_graph(sentiments, times, 'Friday, Jan 27')
print('Friday graph complete.')

sentiments = []
times = []
with open('sat_tweets.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip first line of file (headers)
    next(plots)
    for i, row in enumerate(plots):
        if i % 10 != 0:
            continue
        times.append(row[0])
        sentiments.append(int(classifier.classify(extract_features(row[3].split()))))
GraphMaker.make_graph(sentiments, times, 'Saturday, Jan 28')
print('Saturday graph complete.')
