Building Block (B2) Example Part One

This Building Block project demonstrates a very simple application that can be
deployed to a Blackboard Learn 9.1 application server.

The project expects a user library named Blackboard to be on the 
project's classpath.  That library should include the jar files
needed by a Blackboard Building Block: (bb-platform.jar, 
bb-cms-admin.jar, bb-taglib.jar).

Export the project as a .war file and then install the .war file
in a Blackboard Learn 9.1 application server.  Make the Building
Block available and also set the course tool setting to always
on.

This B2 is a course tool and will display on the Course Tools menu
for any course in Blackboard.

See the bb-manifest.xml file for the Building Block's settings.
When you click on this B2 in Blackboard the index.jsp
will be rendered.


