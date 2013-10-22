# -*- coding: utf-8 -*-
import re
import os
import json
import argparse
import pkg_resources

from telesto.messages import protocol


BLOCK_TEMPLATE = re.compile("(\n|^)(\s*)/\*\{(\w+)\}\*/")
INLINE_TEMPLATE = re.compile("/\*\{(\w+)\}\*/")


def load_template():
    template = json.load(pkg_resources.resource_stream(
        __name__, "templates/template.json"
    ))
    template["packet"] = pkg_resources.resource_string(
        __name__, "templates/packet.java"
    )
    template["superclass"] = pkg_resources.resource_string(
        __name__, "templates/superclass.java"
    )
    return template


def generate_name(template, message):
    return (template['name'].format(name=message.__name__),)


def generate_method_id(template, message):
    return ("0x%02x" % message.method_id,)


def generate_superclass_name(template, message=None):
    return (template['superclass_name'],)


def generate_fields(template, message):
    return aggregate_lines(
        format_lines(template['fields'], field=field.java_name(name),
                     type=field.java_type)
        for name, field in message._fields.iteritems()
        if name != "message_id"
    )


def generate_emit(template, message):
    return aggregate_lines(
        format_lines(template['emit']['types'][field.java_type],
                     field=field.java_name(name))
        for name, field in message._fields.iteritems()
    )


def generate_parse(template, message):
    return aggregate_lines(
        format_lines(template['parse']['types'][field.java_type],
                     field=field.java_name(name))
        for name, field in message._fields.iteritems()
    )

def generate_constructor(template, message):
    return aggregate_lines(
        format_lines(template['assign'], field=field.java_name(name))
        for name, field in message._fields.iteritems()
    )


def generate_constructor_args(template, message):
    return (", ".join("{type} {field}".format(
        field=field.java_name(name), type=field.java_type
    ) for name, field in message._fields.iteritems()),)


def generate_part(template, message, part):
    return GENERATION_FUNCTIONS[part](template, message)


def format_lines(lines, **kwargs):
    if isinstance(lines, basestring):
        lines = [lines]
    return [line.format(**kwargs) for line in lines]


def aggregate_lines(lines):
    return [line for sublines in lines for line in sublines]


def generate_class(file_template, template, message):
    def replace_block(match):
        indent = len(match.group(2))
        variable = match.group(3)
        code = generate_part(template, message, variable)
        return "".join("\n" + " " * indent + line for line in code)

    def replace_inline(match):
        variable = match.group(1)
        code = generate_part(template, message, variable)
        return "\n".join(line for line in code)

    class_ = file_template
    class_ = BLOCK_TEMPLATE.sub(replace_block, class_)
    class_ = INLINE_TEMPLATE.sub(replace_inline, class_)
    return class_


def generate_message_list(template, messages):
    return aggregate_lines(format_lines(
        template['message_list'], id=message.method_id,
        name=generate_name(template, message)[0]
    ) for message in messages if message is not None)


def main():
    parser = argparse.ArgumentParser(description="Generate packet classes.")
    parser.add_argument("output", metavar="DIR", type=str, default=None,
                        help="Folder to store java files in.")

    args = parser.parse_args()

    if not os.path.isdir(args.output):
        parser.error("No such dictionary: {dir}".format(dir=args.output))

    template = load_template()

    superclass = generate_superclass_name(template)[0] + ".java"
    for name in os.listdir(args.output):
        if name.endswith(superclass):
            os.unlink(os.path.join(args.output, name))

    for message in protocol[0]:
        if message is None:
            continue
        code = generate_class(template['packet'], template, message)
        filename = generate_name(template, message)[0] + ".java"

        with open(os.path.join(args.output, filename), "w") as f:
            f.write(code)

    with open(os.path.join(args.output, superclass), "w") as f:
        f.write(generate_class(template['superclass'], template, protocol[0]))


GENERATION_FUNCTIONS = {
    "methodid": generate_method_id,
    "name": generate_name,
    "fields": generate_fields,
    "emit": generate_emit,
    "parse": generate_parse,
    "constructor": generate_constructor,
    "constructorargs": generate_constructor_args,
    "superclass": generate_superclass_name,
    "messages": generate_message_list
}


if __name__ == "__main__":
    main()
