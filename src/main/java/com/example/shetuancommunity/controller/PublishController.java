package com.example.shetuancommunity.controller;


import com.example.shetuancommunity.mapper.QuestionMapper;
import com.example.shetuancommunity.mapper.UserMapper;
import com.example.shetuancommunity.model.Question;
import com.example.shetuancommunity.model.User;
import com.example.shetuancommunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;
   @Autowired
   private UserMapper userMapper;
   @Autowired
   private QuestionService questionService;

   @GetMapping("/publish/{id}")
   public String edit(@PathVariable(name="id") Integer id,
                      Model model){
       Question question=questionMapper.getById(id);
       model.addAttribute("title",question.getTitle());
       model.addAttribute("description",question.getDescription());
       model.addAttribute("tag",question.getTag());
       model.addAttribute("id",question.getId());
       return "publish";
   }

    @GetMapping("/publish")
    public String publish(
    )
    {
        return "publish";
    }


    @PostMapping("/publish")
    public  Object doPublish(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("tag") String tag,
            @RequestParam(value = "id",required = false) Integer id,
            HttpServletRequest request,
            Model model
    )
    {

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        if(title==null||title==""){
            model.addAttribute("error","标题不能为空");
            return  "publish";
        }
        if(description==null||description==""){
            model.addAttribute("error","问题补充不能为空");
            return  "publish";
        }
        if(tag==null||tag==""){
            model.addAttribute("error","标签不能为空");
            return  "publish";
        }


        User user = null;
        String error="用户未注册";
        Cookie[] cookies=request.getCookies();
        model.addAttribute("tag",tag);
        for(Cookie cookie:cookies)
        {
            if (cookie.getName().equals("token"))
            {
                String token=cookie.getValue();
                user=userMapper.findByToken(token);
                if(user!=null)
                {
                    request.getSession().setAttribute("user",user);
                    break;
                }
            }
        }
        if(user==null)
        {
            model.addAttribute("error",error);
            return "publish";
        }


        Question question=new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());

        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";

    }


}
