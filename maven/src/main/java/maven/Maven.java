package maven;

import io.dagger.client.Container;
import io.dagger.client.Directory;
import io.dagger.module.annotation.DefaultPath;
import io.dagger.module.annotation.Function;
import io.dagger.module.annotation.Object;

import java.util.List;

import static io.dagger.client.Dagger.dag;

/**
 * Maven Functions
 */
@Object
public class Maven {

    /**
     * Initialize maven container using the mavenVersion, the distribution and the javaVersion
     *
     * @param directory
     * @param mavenVersion
     * @param distrib
     * @param javaVersion
     * @return
     */
    @Function
    public Container maven(@DefaultPath("/") final Directory directory, final String mavenVersion, final String distrib, final String javaVersion) {
        return dag().container()
                .from(String.format("maven:%s-%s-%s", mavenVersion, distrib, javaVersion))
                .withMountedDirectory("/mnt", directory)
                .withWorkdir("/mnt");
    }

    /**
     * Initialize maven container maven 3.9.9 version, amazoncorretto distribution, and java 21
     *
     * @param directory
     * @return
     */
    @Function
    public Container maven(@DefaultPath("/") final Directory directory) {
        return maven(directory, "3.9.9", "amazoncorretto", "21");
    }

    /**
     * Returns the version of maven
     **/
    @Function
    public Container info(@DefaultPath("/") final Directory directory) {
        return maven(directory)
                .withExec(List.of("mvn", "-version"));
    }

    /**
     * Update version in pom.xml
     */
    @Function
    public Container setVersion(@DefaultPath("/") final Directory directory, final String version) {
        return maven(directory)
                .withExec(List.of("mvn", "versions:set", "-DnewVersion=" + version))
                .withExec(List.of("mvn", "versions:commit"));
    }

    /**
     * Build the application depending au the flag skipTests
     * @param directory
     * @param skipTests
     * @return
     */
    @Function
    public Container build(@DefaultPath("/") final Directory directory, final boolean skipTests) {
        return maven(directory)
                .withExec(List.of("mvn", "package", "-DskipTests=" + skipTests));
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
