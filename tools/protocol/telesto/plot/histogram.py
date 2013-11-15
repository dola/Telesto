#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys

from telesto import plot


CUT_OFF_LEFT = 30000


def main():
    path = sys.argv[1]
    times = {}
    start_time = None

    for row in plot.rows(path, plot.parse_client_line):
        if start_time is None:
            start_time = row['time']
        if row['time'] > start_time + CUT_OFF_LEFT:
            response_time = row['database'] / 1000000
            times[response_time] = times.get(response_time, 0) + 1

    for response_time, count in times.iteritems():
        print response_time, count


if __name__ == "__main__":
    main()
