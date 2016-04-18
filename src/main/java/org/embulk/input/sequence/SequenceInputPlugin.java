package org.embulk.input.sequence;

import java.util.List;

import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.config.TaskReport;
import org.embulk.config.TaskSource;
import org.embulk.spi.Exec;
import org.embulk.spi.InputPlugin;
import org.embulk.spi.PageBuilder;
import org.embulk.spi.PageOutput;
import org.embulk.spi.Schema;
import org.embulk.spi.type.Types;

public class SequenceInputPlugin
        implements InputPlugin
{
    public interface PluginTask
            extends Task
    {
        @Config("from")
        public int getFrom();

        @Config("to")
        public int getTo();

        @Config("step")
        @ConfigDefault("1")
        public int getStep();

        @Config("column_name")
        @ConfigDefault("\"id\"")
        public String getColumnName();
    }

    @Override
    public ConfigDiff transaction(ConfigSource config,
            InputPlugin.Control control)
    {
        PluginTask task = config.loadConfig(PluginTask.class);

        if (task.getStep() == 0) {
            throw new RuntimeException("Step must not be 0");
        }

        Schema schema = new Schema.Builder().add(task.getColumnName(), Types.LONG).build();
        int taskCount = 1;  // number of run() method calls

        return resume(task.dump(), schema, taskCount, control);
    }

    @Override
    public ConfigDiff resume(TaskSource taskSource,
            Schema schema, int taskCount,
            InputPlugin.Control control)
    {
        // TODO: resume support
        control.run(taskSource, schema, taskCount);
        return Exec.newConfigDiff();
    }

    @Override
    public void cleanup(TaskSource taskSource,
            Schema schema, int taskCount,
            List<TaskReport> successTaskReports)
    {
    }

    @Override
    public TaskReport run(TaskSource taskSource,
            Schema schema, int taskIndex,
            PageOutput output)
    {
        PluginTask task = taskSource.loadTask(PluginTask.class);

        int from = task.getFrom();
        int to = task.getTo();
        int step = task.getStep();

        try (final PageBuilder builder = new PageBuilder(Exec.getBufferAllocator(), schema, output)) {
            if (step > 0) {
                for (int i = from; i <= to; i += step) {
                    builder.setLong(0, i);
                    builder.addRecord();
                }
            } else {
                for (int i = from; i >= to; i += step) {
                    builder.setLong(0, i);
                    builder.addRecord();
                }
            }

            builder.finish();
        }

        return Exec.newTaskReport();
    }

    @Override
    public ConfigDiff guess(ConfigSource config)
    {
        return Exec.newConfigDiff();
    }
}
