package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.TbMajorDay;
import com.xiaoshu.entity.TbStuDay;
import com.xiaoshu.entity.Tj;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.TbStuDayService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("stu")
public class TbStuDayController extends LogController {
	static Logger logger = Logger.getLogger(TbStuDayController.class);

	@Autowired
	private TbStuDayService tds;
	@Autowired
	private OperationService operationService;

	@RequestMapping("stuIndex")
	public String index(HttpServletRequest request, Integer menuid) throws Exception {
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);

		List<TbMajorDay> tmdList = tds.querytmd();
		request.setAttribute("tmdList", tmdList);
		return "stu";
	}

	// 查询并分页
	@RequestMapping(value = "stuList", method = RequestMethod.POST)
	public void stuList(HttpServletRequest request, HttpServletResponse response, String offset, String limit,TbStuDay tsd)
			throws Exception {
		try {
			Integer pageSize = StringUtil.isEmpty(limit) ? ConfigUtil.getPageSize() : Integer.parseInt(limit);
			Integer pageNum = (Integer.parseInt(offset) / pageSize) + 1;
			PageInfo<TbStuDay> stuList = tds.findTbStuDayPage(pageNum, pageSize,tsd);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total", stuList.getTotal());
			jsonObj.put("rows", stuList.getList());
			WriterUtil.write(response, jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误", e);
			throw e;
		}
	}

	// 新增或修改
	@RequestMapping("reserveTbStu")
	public void reserveUser(HttpServletRequest request, TbStuDay tsd, String[] hobby, HttpServletResponse response) {
		Integer sdid = tsd.getSdid();
		JSONObject result = new JSONObject();
		try {
			if (sdid != null) { // userId不为空 说明是修改
				tsd.setSdid(sdid);
				String sdhobbys = "";
				for (int i = 0; i < hobby.length; i++) {
					if (i != hobby.length - 1) {
						sdhobbys += hobby[i] + ",";
					} else {
						sdhobbys += hobby[i];
					}
				}
				tsd.setSdhobby(sdhobbys);
				tds.updateTbStuDay(tsd);
				result.put("success", true);
			} else {
				String sdhobbys = "";
				for (int i = 0; i < hobby.length; i++) {
					if (i != hobby.length - 1) {
						sdhobbys += hobby[i] + ",";
					} else {
						sdhobbys += hobby[i];
					}
				}
				tsd.setSdhobby(sdhobbys);
				tds.addTbStuDay(tsd);
				result.put("success", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误", e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteStu")
	public void delStu(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				tds.deleteTbStuDay(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	
	
	@RequestMapping("export")
	public void dc(HttpServletRequest request,HttpServletResponse response) throws Exception {
		JSONObject result=new JSONObject();
		//1.创建工作簿
		HSSFWorkbook wb=new HSSFWorkbook();
		//2.创建工作表
		HSSFSheet sheet=wb.createSheet();
		//3.创建表头数组
		String title[]= {"编号","学生姓名","性别","爱好","生日","所学科目"};
		//4.创建第一行，并循环将数组中的数据写入第一行的的每一个格中
		HSSFRow first=sheet.createRow(0);
		for(int i=0;i<title.length;i++) {
			//得到首行的每一个格并赋值
			first.createCell(i).setCellValue(title[i]);
		}
		//5.查询
		List<TbStuDay> el=tds.dc();
		//6.循环创建行，循环将查到的数据写入格中
		for(int i=0;i<el.size();i++) {
			HSSFRow r=sheet.createRow(i+1);
			r.createCell(0).setCellValue(el.get(i).getSdid());
			r.createCell(1).setCellValue(el.get(i).getSdname());
			r.createCell(2).setCellValue(el.get(i).getSdsex());
			r.createCell(3).setCellValue(el.get(i).getSdhobby());
			r.createCell(4).setCellValue(TimeUtil.formatTime(el.get(i).getSdbirth(), "yyyy-MM-dd"));
			r.createCell(5).setCellValue(el.get(i).getTmd().getMdname());
		}
		//7.1使用下载的方式导出excel数据时配置头信息
//		response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("员工列表.xls", "UTF-8"));
//		response.setHeader("Connection", "close");
//		response.setHeader("Content-Type", "application/octet-stream");
		//7.2使用固定位置导出excel
		OutputStream os;
		File f=new File("d:/aaa.xls");
		os=new FileOutputStream(f);
		//将文件写入到磁盘
		wb.write(os);
		os.close();
		wb.close();
		result.put("success", true);
	}
	
	@RequestMapping("gettj")
	@ResponseBody
	public void getTj(HttpServletRequest request,HttpServletResponse response){
		try {
			List<Tj> l=tds.getTj();
			Object json = JSONObject.toJSON(l);
	        response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("展示错误",e);
		}
	}
	
	@RequestMapping("addzy")
	public void addzy(HttpServletRequest request, HttpServletResponse response,TbMajorDay tmd) {
		JSONObject result = new JSONObject();
		tds.addZy(tmd);
		result.put("success", true);
		WriterUtil.write(response, result.toString());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
