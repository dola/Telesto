#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import argparse

import jinja2

from telesto.messages import protocol


SUPERCLASS = "Packet"


def parse_args():
    parser = argparse.ArgumentParser(description="Generate packet classes.")
    parser.add_argument("protocol", metavar="DIR", type=str, default=None,
                        help="Folder to store protocol classes in.")
    parser.add_argument("handler", metavar="DIR", type=str, default=None,
                        help="Folder to store handling classes in.")

    args = parser.parse_args()

    if not os.path.isdir(args.protocol):
        parser.error("No such dictionary: {dir}".format(dir=args.output))

    if not os.path.isdir(args.handler):
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

    path = os.path.join(folder, message.__name__ + SUPERCLASS + ".java")
    with open(path, "w") as f:
        f.write(code)


def generate_superclass(folder, template, messages):
    print "Generating {}...".format(SUPERCLASS)

    code = template.render(messages=messages, superclass=SUPERCLASS)

    path = os.path.join(folder, SUPERCLASS + ".java")
    with open(path, "w") as f:
        f.write(code)


def generate_handler(folder, template, messages):
    for handler, condition in (
        ("IClientPacketHandler", lambda m: m._accept_from('server')),
        ("IServerLoginPacketHandler", lambda m: m.IS_LOGIN_MESSAGE),
        ("IServerPacketHandler", lambda m: m._accept_from('client') and
                                           m.IS_REGULAR_MESSAGE)
    ):
        print "Generating {}...".format(handler)

        code = template.render(messages=messages, handler=handler,
                               superclass=SUPERCLASS, condition=condition)

        path = os.path.join(folder, handler + ".java")
        with open(path, "w") as f:
            f.write(code)


def main():
    args = parse_args()

    cleanup(args.protocol)

    env = jinja2.Environment(
        loader=jinja2.PackageLoader(__name__, 'templates'),
        extensions=["jinja2.ext.do"]
    )
    packet_template = env.get_template("packet.java")

    for message in protocol[0]:
        generate_packet_class(args.protocol, packet_template, message)

    generate_superclass(args.protocol, env.get_template("superclass.java"),
                        protocol[0])

    generate_handler(args.handler, env.get_template("handler.java"),
                     protocol[0])

    print "\nGenerated {} packet classes.".format(
        sum(1 for m in protocol[0] if m)
    )


if __name__ == "__main__":
    main()
