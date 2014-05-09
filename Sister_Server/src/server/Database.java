package server;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {
	HashMap<String, ArrayList<Data>> tables;

	public Database() {
		tables = new HashMap<String, ArrayList<Data>>();
	}

	public HashMap<String, ArrayList<Data>> getTables() {
		return tables;
	}

	public void setTables(HashMap<String, ArrayList<Data>> tables) {
		this.tables = tables;
	}
	
	public int createTable(String tableName){
		if (getTables().get(tableName) == null) {
			getTables().put(tableName, new ArrayList<Data>());
			return 1;
		} else {
			return -1;
		}
	}
	
	public void insertData(String tableName, Data data) {
		ArrayList<Data> selectedTab = tables.get(tableName);
		int timestamp;
		if(data.timestamp!=-1){
			timestamp = data.timestamp;
		}
		else{
			timestamp = -1;
		}
		for (Data tabData : selectedTab) {
			if (tabData.key.equals(data.key) && tabData.timestamp >= timestamp) {
				timestamp = tabData.timestamp + 1;
			}
		}
		data.timestamp = timestamp;
		selectedTab.add(data);
	}

	public String showTable(String tableName) {
		ArrayList<Data> selectedTab = tables.get(tableName);
		String out = "\n  key  |  value  \n-----------------\n";
		HashMap<String, Data> rows = new HashMap<>();
		for (Data tabData : selectedTab) {
			if (rows.get(tabData.key) == null) {
				rows.put(tabData.key, tabData);
			} else {
				if (rows.get(tabData.key).timestamp < tabData.timestamp) {
					rows.put(tabData.key, tabData);
				}
			}
		}
		for (String key : rows.keySet()) {
			out += "  " + key + "  |  " + rows.get(key).value + "\n";
		}
		return out;
	}
}
