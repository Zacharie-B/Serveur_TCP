package connexion_avec_BD;

import serveur_reseau.ServeurTCP;

public class ConvertStringToHashmap extends ExtractDataHashMap {
	
	public ConvertStringToHashmap() {
		
	}
	
	public void ConvertStringToHashMap(String chaine) {
		//split the String by a comma
		int beginIndex = chaine.indexOf("{");
		int endIndex = chaine.indexOf("}");
		String chaine_hashmap = chaine.substring(beginIndex+1,endIndex);
        String parts[] = chaine_hashmap.split(",");
        
        //iterate the parts and add them to a map
        for(String part : parts){
            //split the employee data by : to get id and name
            String empdata[] = part.split("=");
            
            String strId = empdata[0].trim();
            String strValue = empdata[1].trim();
            
            //add to map
            ServeurTCP.products.put(strId, strValue);
        }
	}
	
}
