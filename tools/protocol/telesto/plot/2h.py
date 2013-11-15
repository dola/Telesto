#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys

from telesto import plot


TIME_STEP = 60000
TIME_SUB_STEP = 1000


def main():
    path = sys.argv[1]

    time = 0
    log_time = None
    sub_log_time = None
    rows = []
    sub_results = []
    counter = {
        'packet': 0,
        'message': 0
    }
    for row in plot.rows(path, plot.parse_client_line):
        if log_time is None:
            log_time = sub_log_time = row['time']

        counter['packet'] += 1
        if row['received'] == 52:
            counter['message'] += 1

        if sub_log_time + TIME_SUB_STEP <= row['time']:
            sub_results.append(counter)
            sub_log_time += TIME_SUB_STEP
            counter = {
                'packet': 0,
                'message': 0
            }

        if log_time + TIME_STEP <= row['time']:
            mean = plot.mean(rows, ('waiting', 'database'))
            dev = plot.standard_deviation(rows, mean)
            mean_by_packet = plot.mean_by_packet(rows, ('database',))
            dev_by_packet = plot.standard_deviation_by_packet(
                rows, mean_by_packet
            )
            sub_mean = plot.mean(sub_results, ('packet', 'message'))
            sub_dev = plot.standard_deviation(sub_results, sub_mean)
            print time,
            print sub_mean['message'], sub_dev['message'],
            print mean['database'],
            print dev['database'],
            print mean_by_packet.get(52, {'database': 0})['database'],
            print dev_by_packet.get(52, {'database': 0})['database'],
            print mean_by_packet.get(3, {'database': 0})['database'],
            print dev_by_packet.get(3, {'database': 0})['database'],
            print mean_by_packet.get(5, {'database': 0})['database'],
            print dev_by_packet.get(5, {'database': 0})['database'],
            print sub_mean['packet'], sub_dev['packet']
            rows = []
            sub_results = []
            time += TIME_STEP
            log_time += TIME_STEP
        rows.append(row)

if __name__ == "__main__":
    main()
