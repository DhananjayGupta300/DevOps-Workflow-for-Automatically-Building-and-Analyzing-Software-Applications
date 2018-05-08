import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;

import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.service.*;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.Project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.io.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gitlab4j.api.models.ProjectHook;
import org.gitlab4j.api.models.Visibility;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;



public class Main {

    // Modify default XML template with custom GitLab URL and custom repository project name
    // Return: customized XML string
    private static String customJenkinsJobXML(String gitlabHostUrl, String projectName){

        // TODO Please update the following variables according to your system
        String UNDERSTAND_PATH = "/home/virenmody/Downloads/scitools/bin/linux64/";
        String CLONED_REPOS_BASE_PATH = "/home/virenmody/ClonedRepos/";

        String jobXml = "<project><actions/><description/><keepDependencies>false</keepDependencies><properties><com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty plugin=\"gitlab-plugin@1.5.3\"><gitLabConnection>GitlabConnection</gitLabConnection></com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty></properties><scm class=\"hudson.plugins.git.GitSCM\" plugin=\"git@3.8.0\"><configVersion>2</configVersion><userRemoteConfigs><hudson.plugins.git.UserRemoteConfig><url>http://10.0.2.15/root/jasmine-maven-plugin.git</url></hudson.plugins.git.UserRemoteConfig></userRemoteConfigs><branches><hudson.plugins.git.BranchSpec><name>*/master</name></hudson.plugins.git.BranchSpec></branches><doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations><browser class=\"hudson.plugins.git.browser.GitLab\"><url>http://10.0.2.15/root/jasmine-maven-plugin</url><version>10.5</version></browser><submoduleCfg class=\"list\"/><extensions/></scm><canRoam>true</canRoam><disabled>false</disabled><blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding><blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding><triggers><com.dabsquared.gitlabjenkins.GitLabPushTrigger plugin=\"gitlab-plugin@1.5.3\"><spec/><triggerOnPush>true</triggerOnPush><triggerOnMergeRequest>true</triggerOnMergeRequest><triggerOnPipelineEvent>false</triggerOnPipelineEvent><triggerOnAcceptedMergeRequest>true</triggerOnAcceptedMergeRequest><triggerOnClosedMergeRequest>true</triggerOnClosedMergeRequest><triggerOnApprovedMergeRequest>true</triggerOnApprovedMergeRequest><triggerOpenMergeRequestOnPush>never</triggerOpenMergeRequestOnPush><triggerOnNoteRequest>true</triggerOnNoteRequest><noteRegex>Jenkins please retry a build</noteRegex><ciSkip>true</ciSkip><skipWorkInProgressMergeRequest>true</skipWorkInProgressMergeRequest><setBuildDescription>true</setBuildDescription><branchFilterType>All</branchFilterType><includeBranchesSpec/><excludeBranchesSpec/><targetBranchRegex/><secretToken>{AQAAABAAAAAQCsHI4NTmA2pXZMGIk7WFudTmTyHod+kacKcpa0XkclE=}</secretToken></com.dabsquared.gitlabjenkins.GitLabPushTrigger></triggers><concurrentBuild>false</concurrentBuild><builders><hudson.tasks.Maven><targets>clean install</targets><usePrivateRepository>false</usePrivateRepository><settings class=\"jenkins.mvn.DefaultSettingsProvider\"/><globalSettings class=\"jenkins.mvn.DefaultGlobalSettingsProvider\"/><injectBuildVariables>false</injectBuildVariables></hudson.tasks.Maven><hudson.tasks.Shell><command>Understand shell script</command></hudson.tasks.Shell></builders><publishers><hudson.tasks.junit.JUnitResultArchiver plugin=\"junit@1.24\"><testResults>target/surefire-reports/*.xml</testResults><keepLongStdio>false</keepLongStdio><healthScaleFactor>1.0</healthScaleFactor><allowEmptyResults>true</allowEmptyResults></hudson.tasks.junit.JUnitResultArchiver><hudson.plugins.jacoco.JacocoPublisher plugin=\"jacoco@3.0.1\"><execPattern>**/**.exec</execPattern><classPattern>**/classes</classPattern><sourcePattern>**/src/main/java</sourcePattern><inclusionPattern/><exclusionPattern/><skipCopyOfSrcFiles>false</skipCopyOfSrcFiles><minimumInstructionCoverage>0</minimumInstructionCoverage><minimumBranchCoverage>0</minimumBranchCoverage><minimumComplexityCoverage>0</minimumComplexityCoverage><minimumLineCoverage>0</minimumLineCoverage><minimumMethodCoverage>0</minimumMethodCoverage><minimumClassCoverage>0</minimumClassCoverage><maximumInstructionCoverage>0</maximumInstructionCoverage><maximumBranchCoverage>0</maximumBranchCoverage><maximumComplexityCoverage>0</maximumComplexityCoverage><maximumLineCoverage>0</maximumLineCoverage><maximumMethodCoverage>0</maximumMethodCoverage><maximumClassCoverage>0</maximumClassCoverage><changeBuildStatus>false</changeBuildStatus><deltaInstructionCoverage>0</deltaInstructionCoverage><deltaBranchCoverage>0</deltaBranchCoverage><deltaComplexityCoverage>0</deltaComplexityCoverage><deltaLineCoverage>0</deltaLineCoverage><deltaMethodCoverage>0</deltaMethodCoverage><deltaClassCoverage>0</deltaClassCoverage><buildOverBuild>false</buildOverBuild></hudson.plugins.jacoco.JacocoPublisher></publishers><buildWrappers><hudson.plugins.ws__cleanup.PreBuildCleanup plugin=\"ws-cleanup@0.34\"><deleteDirs>false</deleteDirs><cleanupParameter/><externalDelete/></hudson.plugins.ws__cleanup.PreBuildCleanup><hudson.plugins.timestamper.TimestamperBuildWrapper plugin=\"timestamper@1.8.9\"/></buildWrappers></project>";
        String customJobXml = null;

        String XMLUrlRepositoryName = gitlabHostUrl + "/root/" + projectName;
        String XMLUrlRepositoryGit = gitlabHostUrl + "/root/" + projectName + ".git";

        String clonedReposPath = CLONED_REPOS_BASE_PATH + projectName + "/";

        String understandScript = "export PATH=$PATH:" + UNDERSTAND_PATH + "\n" +
                                    "und create -db project.udb -languages java\n" +
                                    "und add -db project.udb " + clonedReposPath + "\n" +
                                    "und -db project.udb analyze \n" +
                                    "und -db project.udb report";

        // Parse default jobXml string
        //      (RESOURCE: https://www.ibm.com/developerworks/library/j-pg05199/index.html)
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(jobXml));
            Document doc = db.parse(inputSource);
            NodeList nodeList = doc.getElementsByTagName("*");

            //  Go through each tag, modify necessary tags
            //      (RESOURCE: https://stackoverflow.com/questions/5386991/java-most-efficient-method-to-iterate-over-all-elements-in-a-org-w3c-dom-docume)
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                //Replace project URL path
                if ((node.getNodeType() == Node.ELEMENT_NODE) && (node.getNodeName() == "url")
                        && (node.getParentNode().getNodeName() == "hudson.plugins.git.UserRemoteConfig")) {

                    node.setTextContent(XMLUrlRepositoryGit);
                }
                //Replace project .git file URL path
                else if ((node.getNodeType() == Node.ELEMENT_NODE) && (node.getNodeName() == "url")
                        && (node.getNextSibling().getNodeName() == "version")) {

                    node.setTextContent(XMLUrlRepositoryName);
                }
                //Insert understand program commands
                else if ((node.getNodeType() == Node.ELEMENT_NODE) && (node.getNodeName() == "command")) {

                    node.setTextContent(understandScript);
                }
            }

            // Convert Nodelist with modified tags/content to a String
            // (RESOURCE: https://codereview.stackexchange.com/questions/106480/converting-partial-xml-node-list-to-a-string)
            DOMSource source = new DOMSource();
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            source.setNode(nodeList.item(0));
            transformer.transform(source, result);

            customJobXml = writer.toString();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (TransformerException te) {
            te.printStackTrace();
        }

        return customJobXml;
    }

    // Remove all existing Gitlab projects
    private static void removeAllGitlabProjects(ProjectApi projectApi) {

        try {
            List<Project> projectList = projectApi.getProjects();
            for (Project p : projectList) {
                System.out.println("Deleting Gitlab Project: " + p.getName());
                projectApi.deleteProject(p);
            }
        } catch (GitLabApiException e) {
            System.err.println("Caught Exception for Gitlab Project API: " + e.getMessage());
        }

    }

    // Remove all existing Jenkins jobs
    private static void removeAllJenkinsJobs(JenkinsServer jenkins) {

        try {
            Map<String, Job> jobsMap = jenkins.getJobs();

            for(Job job : jobsMap.values()) {
                System.out.println("Deleting Jenkins Project: " + job.getName());
                jenkins.deleteJob(job.getName());
            }

        } catch (IOException e) {
            System.err.println("Caught IOException for Jenkins jobs retrieval: " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        //TODO: Update the GitLab server URL and API access token for your setup
        String GITLAB_URL = "http://10.0.2.15";
        String GITLAB_API_ACCESS_TOKEN = "XjM25cHKhEm-gYfN5t8Z";
        
        //TODO: Update GitLab username and password according to your GitLab server setup
        String GITLAB_USERNAME = "root";
        String GITLAB_PASSWORD = "rootroot";

        //TODO: Update the localhost/ip:port number according to your Jenkins server setup (localhost = 10.0.2.15)
        String JENKINS_URL = "http://10.0.2.15:8081";

        //TODO: Update Jenkins username and password according to your Jenkins server setup
        String JENKINS_USERNAME = "admin";
        String JENKINS_PASSWORD = "admin";

        // TODO: Update the LOCAL_PATH variable to the directory where you want remote GitHub repositories to be downloaded
        // NOTE: GitHub Repositories are downloaded locally before they are uploaded to your local GitLab server.
        String LOCAL_PATH = "/home/virenmody/ClonedRepos/";

        //Create Gitlab API Object
        GitLabApi gitLabApi = null;
        try {
            gitLabApi = new GitLabApi(GitLabApi.ApiVersion.V3, GITLAB_URL, GITLAB_API_ACCESS_TOKEN);
            System.out.println("Gitlab API Instance = " + gitLabApi);
        } catch (Exception e) {
            System.err.println("Caught Exception for Gitlab API Access: " + e.getMessage());
            e.printStackTrace();
        }

        //Create Gitlab Project Object
        ProjectApi projectApi = null;
        if (gitLabApi != null) {
            projectApi = gitLabApi.getProjectApi();
        }

        //Clean up Gitlab projects from prior builds
        removeAllGitlabProjects(projectApi);

        //Create JenkinsServer Object
        URI jenkinsUri = null;
        JenkinsServer jenkins = null;

        try {
            jenkinsUri = new URI(JENKINS_URL + "/");
            jenkins = new JenkinsServer(jenkinsUri, JENKINS_USERNAME, JENKINS_PASSWORD);
        } catch (URISyntaxException e) {
            System.err.println("Caught URISyntaxException for Jenkins Server Retrieval: " + e.getMessage());
        }

        //Clean up Jenkins jobs from prior runs
        removeAllJenkinsJobs(jenkins);

        //Initialize GitHub API repository Object to download repository from GitHub
        RepositoryService service = new RepositoryService();
        service.getClient();

        //Get list of repository results from GitHub API based on query
        String query = "maven junit language:java";
        List<SearchRepository> searchRepositoryList = null;

        try {
            searchRepositoryList = service.searchRepositories(query);
        }
        catch (IOException e){
            System.err.println("Caught IOException: " + e.getMessage());
        }

        //Create a Jenkins build for each repository in results list
        for (SearchRepository searchRepo: searchRepositoryList) {

            //Get current repository name and owner
            String projectName = searchRepo.getName();
            String username = searchRepo.getOwner();

            String repoURL = "https://github.com/" + username + "/" + projectName + ".git";
            String localDirectory = LOCAL_PATH + projectName;

            // Clone the Github repository to a local folder
            //      (RESOURCE: http://www.vogella.com/tutorials/JGit/article.html)
            Git clonedRepo = null;
            try {
                System.out.println("Cloning GitHub repo " + projectName + " to local directory: " + localDirectory);
                clonedRepo = Git.cloneRepository()
                        .setURI(repoURL)
                        .setDirectory(new File(localDirectory))
                        .call();
            } catch (GitAPIException e) {
                System.err.println("Caught GitAPIException: " + e.getMessage());
            }

            // Create a new empty project on Gitlab
            Project newProject = null;
            String projectDescription = "Pulled from Github.com: " + username + " : " + projectName;

            System.out.println("Creating Gitlab Project: " + projectName + " at " + GITLAB_URL + "/root/"+projectName);
            try {
                newProject = projectApi.createProject(projectName, null, projectDescription, null, null, null, null, Visibility.PUBLIC, null, null);
            } catch (GitLabApiException e) {
                System.err.println("Caught GitlabApiException for Project Creation: " + e.getMessage());
            }

            // Create a Jenkins webhook for the new project
            String webhookUrl = JENKINS_URL + "/project/" + projectName;
            ProjectHook webhook = null;
            System.out.println("Creating Gitlab to Jenkins webhook: " + webhookUrl);
            try {
                webhook = projectApi.addHook(newProject, webhookUrl, true, false, false);
            } catch (GitLabApiException e) {
                System.err.println("Caught GitlabApiException for Webhook: " + e.getMessage());
            }

            // Create a Jenkins Job for the new project
            System.out.println("Creating a Jenkins job for this project: " + JENKINS_URL + "/job/" + projectName);
            try {
                String jobXml = customJenkinsJobXML(GITLAB_URL, projectName);
                jenkins.createJob(projectName, jobXml);
            } catch (IOException e) {
                System.err.println("Caught IOException for Jenkins jobs retrieval: " + e.getMessage());
                e.printStackTrace();
            }

            // Push repository to our local Gitlab server
            System.out.println("Pushing project to Gitlab repository: " + projectName);
            try {

                //  Remove origin from repo
                //      (RESOURCE: https://stackoverflow.com/questions/12799573/add-remote-via-jgit)
                System.out.println("\t - Removing remote Github repo as origin from repo");
                StoredConfig config = clonedRepo.getRepository().getConfig();
                config.unsetSection("remote", "origin");
                config.save();

                // Push the repo to Gitlab
                //      (RESOURCE: https://stackoverflow.com/questions/13446842/how-do-i-do-git-push-with-jgit)
                System.out.println("\t - Adding remote Gitlab repo as origin to repo");
                RemoteAddCommand remoteAddCommand = clonedRepo.remoteAdd();
                remoteAddCommand.setName("origin");
                remoteAddCommand.setUri(new URIish(GITLAB_URL + "/root/" + projectName + ".git"));
                remoteAddCommand.call();

                System.out.println("\t - Pushing repo with proper credentials to Gitlab Server");
                UsernamePasswordCredentialsProvider credProvider = new UsernamePasswordCredentialsProvider(GITLAB_USERNAME, GITLAB_PASSWORD);
                PushCommand pushCommand = clonedRepo.push();
                pushCommand.setCredentialsProvider(credProvider);
                pushCommand.call();

            } catch (GitAPIException e) {
                System.err.println("Caught GitAPIException: " + e.getMessage());
            } catch (URISyntaxException e) {
                System.err.println("Caught URISyntaxException: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }

    }
}
