package com.junhong.forum.common;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.ExcludeDefaultInterceptors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * load the usergroups and roles from the usergroup.xml file Session Bean
 * implementation class UserGroupAndRoleLoaderSingleton
 */
@Singleton
@LocalBean
@Startup
@ExcludeDefaultInterceptors
public class UserGroupAndRoleLoaderSingleton {

	@Inject
	Logger								logger;

	private Map<String, List<String>>	role_usergroup	= new ConcurrentHashMap<String, List<String>>();

	/**
	 * Default constructor.
	 */
	public UserGroupAndRoleLoaderSingleton() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	private void init() {
		try {
			// Step 1: create a DocumentBuilderFactory and setNamespaceAware
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			// Step 2: create a DocumentBuilder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// Step 3: parse the input file to get a Document object

			Document doc = db.parse(new File(getCurrXMLConfigPath()));
			parseNode(doc.getElementsByTagName("roles").item(0));
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}

	}

	private String getCurrXMLConfigPath() {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}

		URL path = loader.getResource("com/junhong/auth/resources/UserGroup_Role.xml");

		if (path == null) {
			logger.info("not able to find UserGroup_Role.xml file");
			return null;

		} else {

			return path.getPath();

		}

	}

	private void parseNode(Node node) {
		String roleName = "";
		List<String> userGroupList = new ArrayList<String>();
		Node current;
		switch (node.getNodeType()) {

		case Node.ELEMENT_NODE:
			String name = node.getNodeName();
			NodeList children = null;
			if (name.equalsIgnoreCase("roles")) {
				// recurse on each child
				children = node.getChildNodes();

				if (children != null) {
					for (int i = 0; i < children.getLength(); i++) {
						parseNode(children.item(i));

					}
				}

			} else if (name.equalsIgnoreCase("role")) {
				// usergroup node
				Node ugnode;
				NamedNodeMap attributes = node.getAttributes();

				for (int i = 0; i < attributes.getLength(); i++) {
					current = attributes.item(i);
					if (current.getNodeName().equalsIgnoreCase("name")) {
						roleName = current.getNodeValue().trim().toUpperCase();
						break;
					}
				}

				children = node.getChildNodes();

				if (children != null) {
					for (int i = 0; i < children.getLength(); i++) {
						ugnode = children.item(i);
						if (ugnode.getNodeName().equalsIgnoreCase("usergroup")) {
							userGroupList.add(ugnode.getTextContent());
						}
					}
				}
				role_usergroup.put(roleName, userGroupList);
			}

		}

		/*
		 * case Node.TEXT_NODE System.out.print(node.getNodeValue()); break;
		 */
	}

	public static void main(String[] args) {
		UserGroupAndRoleLoaderSingleton ins = new UserGroupAndRoleLoaderSingleton();
		ins.init();
		System.out.println(ins.getRole_usergroup());
	}

	public Map<String, List<String>> getRole_usergroup() {
		return role_usergroup;
	}

	public void setRole_usergroup(Map<String, List<String>> role_usergroup) {
		this.role_usergroup = role_usergroup;
	}

	public List<String> getUserGroup(String role) {
		return role_usergroup.get(role);
	}

}
