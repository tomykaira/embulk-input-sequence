Embulk::JavaPlugin.register_input(
  "sequence", "org.embulk.input.sequence.SequenceInputPlugin",
  File.expand_path('../../../../classpath', __FILE__))
