#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import argparse

import jinja2

from telesto.messages import protocol


SUPERCLASS = "Packet"


def parse_args():
    parser = argparse.ArgumentParser(description="Generate packet classes.")
    parser.add_argument("output", metavar="DIR", type=str, default=None,
                        help="Folder to store java files in.")

    args = parser.parse_args()

    if not os.path.isdir(args.output):
        parser.error("No such dictionary: {dir}".format(dir=args.output))

    return args


def cleanup(folder):
    for name in os.listdir(folder):
        if name.endswith("Packet.java"):
            os.unlink(os.path.join(folder, name))


def generate_packet_class(folder, template, message):
    if message is None:
        return

    print "Generating {}{}...".format(message.__name__, SUPERCLASS)

    code = template.render(message=message, superclass=SUPERCLASS)

    filename = message.__name__ + SUPERCLASS + ".java"
    with open(os.path.join(folder, filename), "w") as f:
        f.write(code)


def generate_superclass(folder, template, messages):
    print "Generating {}...".format(SUPERCLASS)

    code = template.render(messages=messages, superclass=SUPERCLASS)

    filename = SUPERCLASS + ".java"
    with open(os.path.join(folder, filename), "w") as f:
        f.write(code)


def main():
    args = parse_args()

    cleanup(args.output)

    env = jinja2.Environment(
        loader=jinja2.PackageLoader(__name__, 'templates'),
        extensions=["jinja2.ext.do"]
    )
    packet_template = env.get_template("packet.java")

    for message in protocol[0]:
        generate_packet_class(args.output, packet_template, message)

    generate_superclass(args.output, env.get_template("superclass.java"),
                        protocol[0])

    print "\nGenerated {} packets.".format(sum(1 for m in protocol[0] if m))


if __name__ == "__main__":
    main()
