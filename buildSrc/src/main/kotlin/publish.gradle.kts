plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("mavenVlado") {
            pom {
                name = "Local Help for GUIs"
                description = "A framework for Java apps that attaches and shows simple help dialogs/wizzards to developer-chosen GUI controls."
                url = "https://github.com/xulman/local-gui-help"
                inceptionYear = "2024"

                organization {
                    name = "Fiji"
                    url = "TBA"
                }

                licenses {
                    license {
                        name = "Simplified BSD License"
                        url = "https://opensource.org/licenses/BSD-2-Clause"
                        distribution = "repo"
                    }
                }

                developers {
                    developer {
                        id = "xulman"
                        name = "Vladimir Ulman"
                        url = "https://imagej.net/people/xulman"
                        roles.addAll("founder", "lead", "developer", "debugger", "reviewer", "support", "maintainer")
                    }
                }

                /*
                                <mailingLists>
                                <mailingList>
                                <name>Image.sc Forum</name>
                                <archive>https://forum.image.sc/</archive>
                                </mailingList>
                                </mailingLists>

                                <scm>
                                <connection>scm:git:https://github.com/xulman/local-gui-help</connection>
                                <developerConnection>scm:git:git@github.com:xulman/local-gui-help</developerConnection>
                                <tag>HEAD</tag>
                                <url>https://github.com/xulman/local-gui-help</url>
                                </scm>
                                <issueManagement>
                                <system>GitHub Issues</system>
                                <url>https://github.com/xulman/local-gui-help/issues</url>
                                </issueManagement>
                                <ciManagement>
                                <system>GitHub Actions</system>
                                <url>https://github.com/xulman/local-gui-help/actions</url>
                                </ciManagement>
                */
            }
        }
    }
}
