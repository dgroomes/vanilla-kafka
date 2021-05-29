#!/usr/bin/env perl
# This is meant to be used in a Bash pipe expression. A command should pipe to a Perl expression that uses this script to
# adorn the incoming line with a timestamp. This is a general purpose way to turn some stream of text data into a timestamped
# log file!
#
# Mostly copied from https://github.com/dgroomes/bash-playground/blob/main/perl/time-with-millis.pl

use Time::HiRes qw(time);
use POSIX qw(strftime);

# Get the current time as a floating point scalar. Seconds from the epoch and fractional seconds. E.g. 1622307523.18013
my $now = time;

# Expand the time scalar into an array of time components (hour, minute, second, etc)
my @timecomponents = localtime $now;

# Format the time components into a human-readable time string
my $timeformatted = strftime "%H:%M:%S", @timecomponents;

# Append milliseconds
$timeformatted .= sprintf ".%03d", ($now-int($now))*1000;

# Print the result!
print $timeformatted, " ", $_;
