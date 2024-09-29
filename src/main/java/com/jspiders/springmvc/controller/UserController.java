
package com.jspiders.springmvc.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jspiders.springmvc.dto.UserDTO;
import com.jspiders.springmvc.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	protected String getSignUpPage() {
		return "signup";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	protected String getLogInPage() {
		return "login";
	}

		/*HttpSession httpSession:

		The HttpSession object represents the current user's session. A session is created when a user first visits the application and lasts until it is invalidated (e.g., when the user logs out or after a timeout).
		In this method, the session is used to check whether the user is logged in by retrieving the user attribute from the session.
		
		ModelMap modelMap:

		ModelMap is a container for data that you want to pass from the controller to the view (typically a JSP, Thymeleaf, etc.). It's used to store attributes that will be available in the view when it's rendered.
		In this case, if the user is logged in, their information (stored as user) will be added to the ModelMap so it can be displayed on the home page.*/

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	protected String getHomePage(HttpSession httpSession, ModelMap modelMap) {
		UserDTO user = (UserDTO) httpSession.getAttribute("user");
		if (user != null) {
			modelMap.addAttribute("user", user);
			return "home";
		} else {
			return "login";
		}
	}

	@RequestMapping(value = "/add-user", method = RequestMethod.POST)
	protected String addUser(@RequestParam(name = "username") String userName,
			@RequestParam(name = "email") String email, @RequestParam(name = "mobile") long mobile,
			@RequestParam(name = "password") String password, @RequestParam(name = "role") String role,
			ModelMap modelMap) {
		UserDTO addedUser = userService.addUser(userName, email, mobile, password, role);
		if (addedUser != null) {
			modelMap.addAttribute("message", "User added..");
			return "login";
		} else {
			modelMap.addAttribute("message", "Something went wrong..");
			return "signup";
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	protected String login(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password,
			ModelMap modelMap, HttpSession httpSession) {
		UserDTO user = userService.login(email, password);
		if (user != null) {
			//The setAttribute method is used to store the user object in the HTTP session.so that we can use when required
			httpSession.setAttribute("user", user);
			httpSession.setMaxInactiveInterval(30 * 24 * 60 * 60);
			modelMap.addAttribute("user", user);
			return "home";
		} else {
			modelMap.addAttribute("message", "Invalid email or password..");
			return "login";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	protected String logout(HttpSession httpSession) {
		httpSession.invalidate();
		return "login";
	}

	@RequestMapping(value = "/edit-user", method = RequestMethod.GET)
	protected String getEditPage(HttpSession httpSession, ModelMap modelMap) {
		UserDTO user = (UserDTO) httpSession.getAttribute("user");
		if (user != null) {
			//use finduserByid because admin also have a authority to edit user so....that's why admin need the user byID
			modelMap.addAttribute("user", userService.findUserById(user.getId()));
			return "edit";
		} else {
			return "login";
		}
	}

	@RequestMapping(value = "/update-user", method = RequestMethod.POST)
	protected String updateUser(@RequestParam(name = "id") int id, @RequestParam(name = "username") String userName,
			@RequestParam(name = "email") String email, @RequestParam(name = "mobile") long mobile,
			@RequestParam(name = "password") String password, ModelMap modelMap, HttpSession httpSession) {
		UserDTO updatedUser = userService.updateUser(id, userName, email, mobile, password);
		if (updatedUser != null) {
			modelMap.addAttribute("message", "User updated..");
		} else {
			modelMap.addAttribute("message", "Something went wrong..");
		}
		
		//    Still pass the session user in case update fail
		UserDTO user = (UserDTO) httpSession.getAttribute("user");
		modelMap.addAttribute("user", user);
		return "home";
	}

	@RequestMapping(value = "/delete-user", method = RequestMethod.GET)
	protected String deleteUser(HttpSession httpSession, ModelMap modelMap) {
		UserDTO user = (UserDTO) httpSession.getAttribute("user");
		if (user != null) {
			userService.deleteUser(user.getId());
			modelMap.addAttribute("message", "User deleted..");
		}
		httpSession.invalidate();
		return "login";
	}
	
// When a client (browser, REST client, etc.) makes a request to the server, it is first received by the DispatcherServlet.
//The DispatcherServlet is the central component that processes all incoming HTTP requests.
	//It examines the request and then delegates it to the appropriate Handler (usually a controller).
	//and the final response is sent back to the client through the DispatcherServlet.
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	protected String findAllUsers(HttpSession httpSession, ModelMap modelMap) {
		UserDTO user = (UserDTO) httpSession.getAttribute("user");
		if (user != null) {
			List<UserDTO> users = userService.findAllUsers();
			modelMap.addAttribute("users", users);
			return "users";
		} else {
			return "login";
		}
	}

//	@RequestMapping(value = "/block-user", method = RequestMethod.GET)
//	protected String blockUser(@RequestParam(name = "id") int id, ModelMap modelMap, HttpSession httpSession) {
//		UserDTO user = (UserDTO) httpSession.getAttribute("user");
//		if (user != null) {
//			UserDTO blockedUser = userService.blockUser(id);
//			if (blockedUser != null) {
//				List<UserDTO> users = userService.findAllUsers();
//				modelMap.addAttribute("users", users);
//			}
//			return "users";
//		} else {
//			return "login";
//		}
//	}
	@RequestMapping(value = "/block-user", method = RequestMethod.GET)
	protected String blockUser(@RequestParam(name = "id") int id, ModelMap modelMap, HttpSession httpSession) {
	    UserDTO user = (UserDTO) httpSession.getAttribute("user");
	    if (user != null) {
	        // Block the user with the specified ID
	        UserDTO blockedUser = userService.blockUser(id);
	        
	        // If the user was successfully blocked
	        if (blockedUser != null) {
	            // Retrieve the updated list of all users (including the blocked status)
	            List<UserDTO> users = userService.findAllUsers();
	            
	            // Pass the list of users to the view (probably a 'users' page)
	            modelMap.addAttribute("users", users);
	        }
	        // Return to the users view page
	        return "users";
	    } else {
	        // If no user is logged in, redirect to the login page
	        return "login";
	    }
	}

	@RequestMapping(value = "/unblock-user", method = RequestMethod.GET)
	protected String unBlockUser(@RequestParam(name = "id") int id, ModelMap modelMap, HttpSession httpSession) {
		UserDTO user = (UserDTO) httpSession.getAttribute("user");
		if (user != null) {
			UserDTO blockedUser = userService.unBlockUser(id);
			if (blockedUser != null) {
				List<UserDTO> users = userService.findAllUsers();	
				modelMap.addAttribute("users", users);
			}
			return "users";
		} else {
			return "login";
		}
	}

}
