# Telesto
A Java Message Passing System. Also check out the [Report](https://github.com/dola/TelestoReport) of the whole project.

## How to run

The runnable JAR file is located in source/release/Telesto.jar accepts the following arguments:

    java -jar Telesto.jar {MW|CL|MC} [options]

The first argument selects between the middleware, the client and the management console respectively.

The client accepts further arguements:

    java -jar Telesto.jar CL [id <client_id>] [name <client_name>] test <test_id>

Where `test_id` can be one of the following (case insensitive):

* ONE_WAY
* REQUEST_RESPONSE_PAIR_CLIENT
* REQUEST_RESPONSE_PAIR_SERVER
* REQUEST_SERVICE
* SERVE_SERVICE

One of `client_id` or `client_name` must be set in order to run successfully.

## Locations

The Java source code is available as an eclipse project in *source*. The scripts used to generate the network packets, run the experiments and evaluate them are located under *source/tools*. The data obtained from the experiments as well as the scripts used to plot the figures in our paper are
available as a compressed archive in *experiments*.
