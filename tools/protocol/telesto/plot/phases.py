#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys

from telesto import plot


OFFSET = 1000  # REMEMBER TO CALCULATE MANUALLY!


def main():
    path = sys.argv[1]

    mean = plot.mean(
        plot.rows(path, plot.parse_middleware_line),
        ("waiting", "parsing", "database", "response")
    )

    dev = plot.standard_deviation(
        plot.rows(path, plot.parse_middleware_line), mean
    )

    print "#",
    for key in mean:
        print key,
    print

    for key in mean:
        print mean[key],
    print

    for key in mean:
        print 100.0 * mean[key] / sum(mean.itervalues()),
    print

    for key in mean:
        print dev[key],
    print



if __name__ == "__main__":
    main()
