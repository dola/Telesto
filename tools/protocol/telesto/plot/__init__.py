#!/usr/bin/env python
# -*- coding: utf-8 -*-
import math


def parse_middleware_line(line):
    t, wait, parse, db, response = line.split("\t")
    return {
        'time': int(t),
        'waiting': int(wait),
        'parsing': int(parse),
        'database': int(db),
        'response': int(response)
    }


def parse_client_line(line):
    t, wait, sent, db, received = line.split("\t")
    return {
        'time': int(t),
        'waiting': int(wait),
        'sent': int(sent),
        'database': int(db),
        'received': int(received)
    }


def rows(path, parser, offset=0):
    with open(path) as f:
        for line in f:
            if offset > 0:
                offset -= 1
                continue
            try:
                yield parser(line)
            except ValueError:
                continue


def mean(lines, keys):
    total = {}
    for key in keys:
        total[key] = 0
    n = 0
    for row in lines:
        for key in keys:
            total[key] += row[key]
        n += 1
    mean = {}
    for key in keys:
        mean[key] = total[key] / n if n > 0 else 0
    return mean


def mean_by_packet(lines, keys):
    total = {}
    for key in keys:
        total[key] = {}
    n = {}
    for row in lines:
        method = row['received']
        for key in keys:
            total[key][method] = total[key].get(method, 0) + row[key]
        n[method] = n.get(method, 0) + 1
    mean = {}
    for method in n:
        mean[method] = {}
        for key in keys:
            mean[method][key] = (total[key][method] / n[method]
                                 if n[method] > 0 else 0)
    return mean


def standard_deviation(lines, mean):
    variation = {}
    for key in mean:
        variation[key] = 0
    n = -1
    for row in lines:
        for key in mean:
            variation[key] += (row[key] - mean[key]) ** 2
        n += 1
    dev = {}
    for key in mean:
        dev[key] = int(math.sqrt(variation[key] / n))
    return dev


def standard_deviation_by_packet(lines, mean):
    variation = {}
    keys = mean[mean.keys()[0]].keys()
    for key in keys:
        variation[key] = {}
        for method in mean:
            variation[key][method] = 0
    n = {}
    for method in mean:
        n[method] = -1
    for row in lines:
        method = row['received']
        for key in keys:
            variation[key][method] += (row[key] - mean[method][key]) ** 2
        n[method] += 1
    dev = {}
    for method in n:
        dev[method] = {}
        for key in keys:
            dev[method][key] = int(
                math.sqrt(variation[key][method] / n[method])
            ) if n[method] > 0 else 0
    return dev
