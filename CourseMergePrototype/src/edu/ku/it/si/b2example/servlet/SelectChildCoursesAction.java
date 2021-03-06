package edu.ku.it.si.b2example.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blackboard.data.ValidationException;
import blackboard.data.course.Course;
import blackboard.data.course.CourseCourseManager;
import blackboard.data.course.CourseCourseManagerFactory;
import blackboard.data.course.FailedCrossListEnrollments;
import blackboard.persist.BbPersistenceManager;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManager;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.persistence.PersistenceServiceFactory;

/**
 * Get the child course(s) the user selected on the view page and forward user
 * to the confirm view page.
 */
public class SelectChildCoursesAction extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SelectChildCoursesAction.class.getName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {

			ContextManager contextManager = ContextManagerFactory.getInstance();

			/*
			 * See:
			 * http://library.blackboard.com/ref/598135ae-501e-46f6-9910-190d
			 * 7ea0a17c/blackboard/platform/context/Context.html
			 */
			Context ctx = contextManager.getContext();

			/*
			 * See:
			 * http://library.blackboard.com/ref/598135ae-501e-46f6-9910-190d
			 * 7ea0a17c/blackboard/data/course/Course.html
			 */
			Course course = ctx.getCourse();

			if (course != null) {

				LOGGER.info("Title of parent course retrieved using Context is "
						+ course.getTitle());

			} else {

				LOGGER.warn("No course was retrieved using Context.");

				throw new IllegalStateException(
						"No course was retrieved using Context. Unable to identify the parent course.");
			}

			List<Course> childCourseList = getChildCourses(request, course);

			// TODO: Loop over the collection of child Course objects 
			// and add each child course to the parent course
			// using CourseCourseManager method addChildToMaster.
			// Note the method addChildToMaster returns an object of 
			// type FailedCrossListEnrollments.  That class is not 
			// documented in the Blackboard API JavaDoc.  Using method 
			// getAllFailedEnrollmentMessages() of that class you can
			// get a collection of Strings, where each String is a 
			// message about a failed enrollment update to the parent 
			// course.
			//See:  http://library.blackboard.com/ref/598135ae-501e-46f6-9910-190d7ea0a17c/blackboard/data/course/CourseCourseManager.html
			//and http://library.blackboard.com/ref/598135ae-501e-46f6-9910-190d7ea0a17c/blackboard/data/course/CourseCourseManagerFactory.html
			
//			CourseCourseManager courseCourseManager = CourseCourseManagerFactory.getInstance() ;
//
//			for (Course childCourse : childCourseList) {
//				
//				try {
//					
//					FailedCrossListEnrollments failedCrossListEnrollments = courseCourseManager.addChildToMaster(childCourse.getId(), course.getId());
//						
//					if (failedCrossListEnrollments.getAllFailedEnrollmentMessages().size() > 0) {
//					
//					    LOGGER.warn("Child course " + childCourse.getCourseId() + " was merged with parent course " + 
//					      course.getCourseId() + " successfully but there were enrollment errors.");
//					    
//						logAnyFailedEnrollments(failedCrossListEnrollments);
//					    
//					} else {
//						
//						LOGGER.info("Child course " + childCourse.getCourseId() + " was merged with parent course " + 
//							      course.getCourseId() + " successfully and there were NO enrollment errors.");
//
//					}
//					
//				
//				} catch (ValidationException e) {
//						
//						LOGGER.error(e.getMessage());
//
//						RequestDispatcher dispatcher = getServletContext()
//								.getRequestDispatcher("/error.jsp");
//
//						dispatcher.forward(request, response);
//						
//					}	
//				
//			}

			request.setAttribute("childCourses", childCourseList);

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/mergecoursessuccess.jsp");

			dispatcher.forward(request, response);

		} catch (PersistenceException ex) {

			LOGGER.error(ex.getMessage());

			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/error.jsp");

			dispatcher.forward(request, response);

		}

	}

	/**
	 * Get the child Blackboard courses users confirmed
	 * to merge with the Parent course.
	 * @param request - HTTPServletRequest - contains the child course Ids
	 * that were hidden fields on the confirm view page.
	 * @param course - parent Blackboard course
	 * @return Collection of Course objects representing the child
	 * Blackboard courses to merge with the parent course.
	 * @throws PersistenceException
	 * @throws KeyNotFoundException
	 */
	private List<Course> getChildCourses(HttpServletRequest request,
			Course course) throws PersistenceException, KeyNotFoundException {
		BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance()
				.getDbPersistenceManager();

		CourseDbLoader courseDbLoader = (CourseDbLoader) bbPm
				.getLoader(CourseDbLoader.TYPE);

		String[] selectedCourseIdArray = request
				.getParameterValues("childCourseId");

		List<Course> childCourseList = new ArrayList<Course>();

		for (String courseId : selectedCourseIdArray) {

			LOGGER.info("User confirmed " + courseId
					+ " as a child course to merge with parent course "
					+ course.getCourseId());

			Course aCourse = courseDbLoader.loadByCourseId(courseId);

			childCourseList.add(aCourse);

		}
		
		return childCourseList;
		
	}
	
	
	/**
	 * Log any failed enrollments that occurred when the child course
	 * was added to the master course.
	 * @param failedCrossListEnrollments
	 */
	private void logAnyFailedEnrollments(
			FailedCrossListEnrollments failedCrossListEnrollments) {
		
		
		
		for (String failedEnrollmentMessage : failedCrossListEnrollments.getAllFailedEnrollmentMessages() ) {

			LOGGER.warn("Enrollment failed:  " + failedEnrollmentMessage ) ;
			
		}
		
	}

}
