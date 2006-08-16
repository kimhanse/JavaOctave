
if (strcmp(octave_config_info.version, "2.9.5"))
  crash_dumps_octave_core = 0;
  sigterm_dumps_octave_core = 0;
elseif (strcmp(octave_config_info.version, "2.9.6") || strcmp(octave_config_info.version, "2.9.7"))
  crash_dumps_octave_core(0);
  sigterm_dumps_octave_core(0);
else
  warning("Working with unknown version of Octave.");
endif
