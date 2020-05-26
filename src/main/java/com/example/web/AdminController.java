package com.example.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.entities.AcheteurPublic;
import com.example.entities.Admin;
import com.example.entities.AppelOffres;
import com.example.entities.Document;
import com.example.entities.Soumissionnaire;
import com.example.metier.MarchesPublicsMetier;

@Controller
public class AdminController {
	@Autowired
	private MarchesPublicsMetier metier;
	
	
	@RequestMapping(value="/portailAdmin")
    public String pageAdmin(){
        return "portailAdmin";
    }
	
	@GetMapping("/users")
	public String getUsers() {
		return "users";
	}
	
	@RequestMapping(value="/addAdmin")
    public String newAdmin(Model model){
		Admin user = new Admin();
		model.addAttribute("user",user);
        return "addAdmin";
    }
	
	@RequestMapping(value="/addAP")
    public String newAP(Model model){
		AcheteurPublic user = new AcheteurPublic();
		model.addAttribute("user",user);
        return "addAP";
    }
	
	@RequestMapping(value="/addSM")
    public String newSM(Model model){
		Soumissionnaire user = new Soumissionnaire();
		model.addAttribute("user",user);
        return "addSM";
    }
	
	@RequestMapping(value="/listAdmin")
	public String listAdmin(Model model) {
		List <Admin> admins = metier.getAdmins();
		model.addAttribute("admins", admins);
		return "listAdmin";
	}
	
	@RequestMapping(value="/listAP")
	public String listAP(Model model) {
		List <AcheteurPublic> aps = metier.getAPs();
		model.addAttribute("aps", aps);
		return "listAP";
	}
	
	@RequestMapping(value="/listSM")
	public String listSM(Model model) {
		List <Soumissionnaire> sms = metier.getSMs();
		model.addAttribute("sms", sms);
		return "listSM";
	}
	
	@RequestMapping(value="/saveAdmin", method = RequestMethod.POST)
	public String saveAdmin(@ModelAttribute("user")Admin user) {
		user.setPassword(metier.generatedPassword());
		metier.saveAdmin(user);
		return "redirect:/listAdmin";
	}
	
	@RequestMapping(value="/saveAP", method = RequestMethod.POST)
	public String saveAP(@ModelAttribute("user")AcheteurPublic user) {
		user.setPassword(metier.generatedPassword());
		metier.saveAP(user);
		return "redirect:/listAP";
	}
	@RequestMapping(value="/saveSM", method = RequestMethod.POST)
	public String saveSM(@ModelAttribute("user")Soumissionnaire user) {
		user.setPassword(metier.generatedPassword());
		metier.saveSM(user);
		return "redirect:/listSM";
	}
	
	@RequestMapping(value="/changePassword" ,method = RequestMethod.POST )
    public String changePassword(Model model, Long id, String role) {
		try {
			String password = metier.generatedPassword();
			metier.changePassword(password, id);
		}catch (Exception e){
	          model.addAttribute("error",e);
	          return "redirect:/list"+role+"&error="+e.getMessage();
	    }
	    return "redirect:/list"+role;
	}
	
	@RequestMapping(value="/changeEmail" ,method = RequestMethod.POST )
    public String changeEmail(Model model, String email , Long id, String role) {
		try {
			metier.changeEmail(email, id);
		}catch (Exception e){
	          model.addAttribute("error",e);
	          return "redirect:/list"+role+"&error="+e.getMessage();
	    }
	    return "redirect:/list"+role;
		
	}
	
	@RequestMapping(value="/changeActive" ,method = RequestMethod.POST )
    public String changeActive(Model model, Boolean active, Long id, String role) {
		try {
			metier.changeActive(active, id);
		}catch (Exception e){
	          model.addAttribute("error",e);
	          return "redirect:/list"+role+"&error="+e.getMessage();
	    }
	    return "redirect:/list"+role;
		
	}
	
	@RequestMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable(name = "id") Long id) {
		metier.deleteUser(id);
		return "redirect:/users";
	}
	
	@RequestMapping("/editAdmin/{id}")
	public ModelAndView showEditAdminForm(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("editAdmin");
		Admin user = metier.getAdmin(id);
		mav.addObject("user", user);
		return mav;
	}
	
	@RequestMapping("/editAP/{id}")
	public ModelAndView showEditAPForm(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("editAP");
		AcheteurPublic user = metier.getAP(id);
		mav.addObject("user", user);
		return mav;
	}
	
	@RequestMapping("/editSM/{id}")
	public ModelAndView showEditSMForm(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("editSM");
		Soumissionnaire user = metier.getSM(id);
		mav.addObject("user", user);
		return mav;
	}
	
	@RequestMapping(value = "/gestionAOAdmin/{email}", method = { RequestMethod.GET, RequestMethod.POST })
	public String gestionAOAdmin(Model model,  @PathVariable(name = "email") String email,  @ModelAttribute("selectedSecteur") AppelOffres selectedSecteur) {
		Admin admin = metier.getAdminByEmail(email);
		model.addAttribute("admin", admin);
		
		ArrayList<String> listSecteurs = metier.listSecteurs();
		model.addAttribute("listSecteurs", listSecteurs);
		
		List<AppelOffres> listAOSecteur = metier.listAOBySecteur(selectedSecteur.getSecteurAO());
		model.addAttribute("listAOSecteur", listAOSecteur);
		
		model.addAttribute("listAOSecteur", listAOSecteur);
		return "gestionAOAdmin";
	}
	
	@RequestMapping("/deleteAOAdmin/{codeAO}&{id}")
	public String deleteAOAdmin(@PathVariable(name = "codeAO") Long codeAO, @PathVariable(name = "id") Long id) {
		AppelOffres ao = metier.getAO(codeAO).get();
		String secteur = ao.getSecteurAO();
		metier.deleteAO(codeAO);
		Admin admin = metier.getAdmin(id);
		return "redirect:/gestionAOAdmin/"+admin.getEmail()+"?secteurAO="+secteur;
	}
	
	@GetMapping("/docs")
	public String getDocs(Model model) {
		List <Document> docs = metier.getFiles();
		model.addAttribute("docs", docs);
		return "documents";
	}
	
	@PostMapping("/uploadFiles")
	public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		for(MultipartFile file:files) {
			metier.saveFile(file);
		}
		return "redirect:/docs";
	}
	
	@GetMapping("/downloadFile/{fileId}")
	public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) {
		Document doc = metier.getFile(fileId).get();
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(doc.getTypeDoc()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\""+doc.getNomDoc()+"\"")
				.body(new ByteArrayResource(doc.getData()));
	}
	
	@RequestMapping("/delete/{id}")
	public String deleteDocument(@PathVariable(name = "id") Long id) {
		metier.deleteDoc(id);
		return "redirect:/docs";
	}
	
	@RequestMapping(value = "/profilAdmin/{email}")
	public String profilAdmin(Model model, @PathVariable(name = "email") String email) {
		Admin admin = metier.getAdminByEmail(email);
		model.addAttribute("admin", admin);
		return "profilAdmin";
	}
}
