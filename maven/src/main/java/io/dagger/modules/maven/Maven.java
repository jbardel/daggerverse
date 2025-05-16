package io.dagger.modules.maven;

import io.dagger.client.Container;
import io.dagger.client.DaggerQueryException;
import io.dagger.client.Directory;
import io.dagger.module.annotation.DefaultPath;
import io.dagger.module.annotation.Function;
import io.dagger.module.annotation.Object;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.dagger.client.Dagger.dag;

/**
 * Maven main object
 */
@Object
public class Maven {
    /**
     * Returns a container that echoes whatever string argument is provided
     */
    @Function
    public Container containerEcho(String stringArg) {
        return dag().container().from("alpine:latest").withExec(List.of("echo", stringArg));
    }

    /**
     * Returns lines that match a pattern in the files of the provided Directory
     */
    @Function
    public String grepDir(Directory directoryArg, String pattern)
            throws InterruptedException, ExecutionException, DaggerQueryException {
        return dag()
                .container()
                .from("alpine:latest")
                .withMountedDirectory("/mnt", directoryArg)
                .withWorkdir("/mnt")
                .withExec(List.of("grep", "-R", pattern, "."))
                .stdout();
    }

    @Function
    public Container maven(@DefaultPath("/") final Directory directory) {
        return dag()
                .container()
                .from("maven:3.9.9-amazoncorretto-21")
                .withMountedDirectory("/mnt", directory)
                .withWorkdir("/mnt");
    }

    /**
     * Returns the version of maven
     **/
    @Function
    public Container version(@DefaultPath("/") final Directory directory) {
        return maven(directory)
                .withExec(List.of("mvn", "-version"));
    }

    /**
     * Runs a command in the maven container
     */
    @Function
    public Container runCommand(@DefaultPath("/") final Directory directory, final List<String> command) {
        return maven(directory)
                .withExec(command);
    }

}
