package com.cglee079.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cglee079.log.Log;
import com.cglee079.model.Fish;
import com.cglee079.model.User;
import com.cglee079.service.FishService;

@Controller
public class FishController {
	private FishService fishService;

	@Autowired
	public void setFishService(FishService fishService) {
		this.fishService = fishService;
	}

	@RequestMapping(value = "/saveFish")
	public void saveFish(Model model, Fish fish) {
		Log.line();
		Log.i("## save fish");
		Log.i(fish.toString());
		
		/* 시연 미스 */
//		String date	 		= fish.getDate();
//		String dateDiv[] 	= date.split("/");
//		String newDate 		= dateDiv[0].trim()+ "/" + dateDiv[1].trim() + "/" + dateDiv[2].trim();
//		Log.i(newDate);
//		fish.setDate(newDate);
		
		fishService.insert(fish);
	}

	@RequestMapping(value = "/deleteFish")
	public void deleteFish(HttpSession session, String id) {
		Log.line();
		Log.i("## delete fish");
		Log.i("id: " + id);
		Log.i("# delete start");
		
		Fish fish = fishService.getFish(id);
		String filename	= fish.getImageFile();
		String path 	= session.getServletContext().getRealPath("/resources/images");
		
		File	fshImg 		= new File(path, filename);
		boolean fshdel		= false;
		boolean fshImgdel 	= false;
		
		fshdel =  fishService.delete(id);
		if (fshImg.exists()) { fshImgdel = fshImg.delete(); }
		
		/* */
		if (fshdel) { Log.i("info deleted"); }
		else { Log.i("info deleted fail"); }
		
		if (fshImgdel) { Log.i("image deleted"); }
		else { Log.i("image deleted fail"); }
		
		Log.i("# delete end");
	}

	@RequestMapping(value = "/saveFishImage")
	public void saveFishImage(HttpSession session, String id, String filename, @RequestParam("image") MultipartFile multipartFile) throws IllegalStateException, IOException {
		Log.line();
		Log.i("## save fish image");
		Log.i("id : " + id);
		Log.i("filename : " + filename);
		Log.i("filesize  :" + multipartFile.getSize());
		Log.i("# save start");

		String path	= session.getServletContext().getRealPath("/resources/images");
		File fshImg = new File(path, filename);

		multipartFile.transferTo(fshImg);

		Log.i("# save end");
	}

	//mybatis
	@ResponseBody
	@RequestMapping(value = "/selectMyFish", method = { RequestMethod.GET, RequestMethod.POST })
	public HashMap<String, Object> showFishsByID(HttpSession session, String fishname) {
		Log.line();
		Log.i("## select my fishs");

		String id = (String) session.getAttribute("id");
		
		/* select my fish list */
		List<Fish> fishs = fishService.getFishsByOwner(id);
		Log.i("selected database");
		
		JSONArray fishArray	= new JSONArray();
		if (fishs != null) {
			int size	= fishs.size();
			for (int i = 0; i < size; i++) {
				fishArray.add(i, fishs.get(i).toJSONStr());
			}
		}

		HashMap<String, Object> response = new HashMap<>();
		response.put("fishs", fishArray.toString());
		return response;
	}

	//mybatis
	@ResponseBody
	@RequestMapping(value = "/selectAllFish")
	public HashMap<String, Object> showAllFish(String stDate, String endDate, String species) {
		Log.line();
		Log.i("## select all fishs");
		Log.i("stDate	= " + stDate);
		Log.i("endDate	= " + endDate);
		Log.i("Species	= " + species);

		List<Fish> fishs = fishService.getFishsInPeriodBySpecies(species, stDate, endDate);

		Log.i("get Species Fish From DataBase" + species);

		JSONArray 	fishArray 	= new JSONArray();
		if (fishs != null) {
			int size	= fishs.size();
			for (int i = 0; i < size; i++) {
				fishArray.add(i, fishs.get(i).toJSONStr());
			}
		}
		
		HashMap<String, Object> response = new HashMap<>();
		response.put("fishs", fishArray);

		return response;
	}
}