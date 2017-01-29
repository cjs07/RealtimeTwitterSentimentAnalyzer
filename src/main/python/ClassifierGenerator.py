import csv
import nltk
import time
import pickle


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


# Coverts words to their root word
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
# Tell the machine learning algorithm that this is our training set of data
training_set = nltk.classify.apply_features(extract_features, filtered_tweets)

print('Starting to train data using Naive Bayes classifier!')
classifier = nltk.NaiveBayesClassifier.train(training_set)
print('Done training the data!')

# Lists the words that are the most informative as to whether we are looking at a positive or negative tweet
classifier.show_most_informative_features(50)

# Save the classifier to be used later so we don't have to train it every time
save_classifier = open('classifier.pickle', 'wb')
pickle.dump(classifier, save_classifier)
save_classifier.close()

# Test the accuracy of the classifier
correct = 0
incorrect = 0
attempts = 0
with open('sentiment_analysis_dataset.csv', 'r', encoding='utf-8', errors='ignore') as csvfile:
    plots = csv.reader(csvfile, delimiter=',')
    # Skip headers
    next(plots)
    for i in range(TRAINING_AMOUNT):
        next(plots)
    for i, row in enumerate(plots):
        if i == 50000:
            break
        actual_sentiment = row[1]
        predicted_sentiment = classifier.classify(extract_features(s.stem(row[3]).split()))
        if actual_sentiment == predicted_sentiment:
            correct += 1
            attempts += 1
        else:
            incorrect += 1
            attempts += 1
        if i % 1000 == 0:
            print('%i' % i + ' lines into the file')
accuracy = correct / attempts
end_time = time.clock()
print("For a training set of " + str(TRAINING_AMOUNT) + " samples, the accuracy across " + str(
    attempts) + " predictions was " + str(round(accuracy * 100, 2)) + "%")
print("The code took " + str(round(end_time - start_time, 2)) + " seconds to run.")
