#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
import math

from telesto import plot


TIME_STEP = 1000
CUT_OFF_LEFT = 30000
CUT_OFF_RIGHT = 10000


def main():
    path = sys.argv[1]
    log_time = None
    counter = {
        'packet': 0,
        'message': 0
    }
    results = []

    for row in plot.rows(path, plot.parse_client_line):
        if log_time is None:
            log_time = row['time']

        counter['packet'] += 1
        if row['received'] == 52:
            counter['message'] += 1

        if log_time + TIME_STEP <= row['time']:
            results.append(counter)
            log_time += TIME_STEP
            counter = {
                'packet': 0,
                'message': 0
            }

    reponse_time = plot.mean(
        plot.rows(path, plot.parse_client_line), ('database',)
    )

    reponse_time_dev = plot.standard_deviation(
        plot.rows(path, plot.parse_client_line), reponse_time
    )

    results = results[CUT_OFF_LEFT / TIME_STEP:-CUT_OFF_RIGHT / TIME_STEP]

    mean = plot.mean(
        results, ("packet", "message")
    )

    dev = plot.standard_deviation(
        results, mean
    )

    confidence = 1.960 * dev['message']
    confidence /= math.sqrt(len(results))

    print mean['packet'],
    print dev['packet'],
    print mean['message'],
    print dev['message'],
    print reponse_time['database'],
    print reponse_time_dev['database'],
    print confidence,
    print confidence / mean['message']


if __name__ == "__main__":
    main()
