SETUP

clone project via git:
git clone https://gitlab.informatik.haw-hamburg.de/acm758/gkap.git

IMPORT TO INTELLIJ

Open IntelliJ->File->Open
Then navigate to folder and open

RUN THE PROJECT

Project requires at least java 10 to run
If you have java as standard you may remove the following line in pom.xml file
\<executable>C:\ProgramFiles\Java\jdk-11.0.2\bin\java.exe\</executable>

Otherwise please update the path inside \<executable> to your java 10 (or higher) executable

To run the application you have to run maven.
On the right side in IntelliJ UI there should be a tab called "Maven Projects"
Expand it, open plugins->javafx tab, run compile job and then run 'run' job to launch application

