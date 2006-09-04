#!/usr/bin/perl

use strict;
use warnings;

my $sigs = 0;
sub sig_term {
    ++$sigs;
    print "SIGTERM caught ($sigs)\n";
}
$SIG{TERM} = \&sig_term;

print "kill $$\n";

print "Start\n";
system("sleep 600");
print "End\n";

