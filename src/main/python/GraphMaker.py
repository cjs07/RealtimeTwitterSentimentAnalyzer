import csv
import matplotlib.pyplot as plt
import numpy as np


def make_graph(sentiments, post_times, day):
    times = []
    # Converts each time from parameter into a graphable value and appends it to  times
    for time in post_times:
        times.append(time_as_float(time_in_est(time[11:19])))
    # Find the number of tweets posted at each time interval that are positive and negative
    time_set = set(times)
    time_set = sorted(time_set)
    times = sorted(times)
    pos_tot = []
    neg_tot = []
    prev_i = 0
    for s in time_set:
        i = prev_i
        pos = 0
        neg = 0
        while i < len(times) and times[i] == s:
            if sentiments[i] == 0:
                neg += 1
            elif sentiments[i] == 1:
                pos += 1
            i += 1
        pos_tot.append(pos)
        neg_tot.append(neg)
        prev_i = i

    x = np.array(time_set)
    y = np.array(pos_tot)
    z = np.array(neg_tot)

    plt.clf()
    plt.style.use('bmh')
    plt.plot(x, y, label='Positive Tweets', linewidth=1)
    plt.plot(x, z, label='Negative Tweets', linewidth=1)
    plt.legend(loc='best')
    plt.title('Number of Tweets vs. Time (' + day + ')')
    plt.xlabel('Time (Hours since Midnight, EST)')
    plt.ylabel('Number of Tweets')
    plt.xlim(0, 24)
    plt.ylim(0)
    plt.grid()
    plt.tight_layout()
    plt.savefig(day + '.png', dpi=200)


def time_as_float(time):
    hour = int(time[0:2])
    decimal = float(time[3:5]) / 60
    return hour + decimal


def time_in_est(time):
    new_hour = int(time[0:2])-5
    if new_hour < 0:
        new_hour += 24
    if new_hour < 10:
        hour_string = '0%i' % new_hour
    else:
        hour_string = '%i' % new_hour
    s = hour_string + time[2:]
    return s
