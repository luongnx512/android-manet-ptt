package ro.ui.pttdroid.groups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GroupHelper {
	
	public static final String GROUP_PREFS = "groups";
	public static final String GROUP_INDEX = "group_index"; // array index, not group id
	
	public static final String GROUP_NAME  = "group_name";
	public static final String GROUP_LIST  = "group_list";
	
	private static Map<Integer, Group> groupMap = null;
	
	private static SharedPreferences prefs = null;
	
	private static int nextId = -1;
	
	public static void getSettings(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		loadGroups();
	}
	
	private static void loadGroups() {
		groupMap = new HashMap<Integer, Group>(); // TODO: sort alphabetically
		
		int id = 0;
		while (true) {
			if (prefs.contains(GROUP_NAME + "." + id)) {
				String name = prefs.getString(GROUP_NAME + "." + id, "");
				String list = prefs.getString(GROUP_LIST + "." + id, "");
	
				ArrayList<String> peers = new ArrayList<String>();
				peers.addAll(Arrays.asList(list.split(",")));
				
				Group group = new Group(id, name, peers);
				groupMap.put(id, group);
			} else {
				break; // stop search
			}
			id++;
		}
		
		nextId = id; // next group id
	}
	
	public static List<Group> getGroups() {
		List<Group> groups = new ArrayList<Group>(groupMap.values());
		return groups;
	}
	
	public static Group getGroup(int index) {
		return groupMap.get(index);
	}
	
	public static String[] getNames(List<Group> groups) {
		String[] names = new String[groups.size()];
		for (int i = 0; i < groups.size(); i ++) {
			names[i] = groups.get(i).name;
		}
		return names;
	}
	
	public static Group createGroup(String name, List<String> peers) {
		String items = "";
		for (int i = 0; i < peers.size(); i++) {
			items += peers.get(i) + ",";
		}
		items = items.substring(0, items.length()-1);
		
		Group group = new Group(nextId, name, peers);
				
		SharedPreferences.Editor prefEditor = prefs.edit();

		prefEditor.putString(GROUP_NAME + "." + nextId, name);
		prefEditor.putString(GROUP_LIST + "." + nextId, items);

		prefEditor.commit();
		
		groupMap.put(nextId, group);
		
		nextId++; // next group id
		
		return group;
	}
	
	public static void deleteGroup(int id) {
		SharedPreferences.Editor prefEditor = prefs.edit();
		
		prefEditor.remove(GROUP_NAME + "." + id);
		prefEditor.remove(GROUP_LIST + "." + id);

		prefEditor.commit();
		
		groupMap.remove(id);
	}
}