package com.sm.graduation.root.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sm.graduation.accident.pojo.AccidentRecord;
import com.sm.graduation.accident.service.AccidentRecordService;
import com.sm.graduation.admin.pojo.AdminInfo;
import com.sm.graduation.admin.service.AdminInfoService;
import com.sm.graduation.checkin.pojo.CheckIn;
import com.sm.graduation.checkin.service.CheckInService;
import com.sm.graduation.common.loginpojo.LoginPojo;
import com.sm.graduation.common.result.AjaxResult;
import com.sm.graduation.dormitory.pojo.DormitoryAllocation;
import com.sm.graduation.dormitory.service.DormitoryAllocationService;
import com.sm.graduation.food.pojo.MonthlyCatering;
import com.sm.graduation.food.service.MonthlyCateringService;
import com.sm.graduation.health.pojo.HealthRecords;
import com.sm.graduation.health.service.HealthRecordsService;
import com.sm.graduation.high.pojo.HighRisk;
import com.sm.graduation.high.service.HighRiskService;
import com.sm.graduation.medication.pojo.Medication;
import com.sm.graduation.medication.service.MedicationService;
import com.sm.graduation.nurse.pojo.Nursing;
import com.sm.graduation.nurse.service.NursingService;
import com.sm.graduation.older.pojo.OlderInfo;
import com.sm.graduation.older.service.OlderInfoService;
import com.sm.graduation.out.pojo.GoOut;
import com.sm.graduation.out.service.GoOutService;
import com.sm.graduation.usr.pojo.UsrInfo;
import com.sm.graduation.usr.service.UsrInfoService;
import com.sm.graduation.visitor.pojo.Visitor;
import com.sm.graduation.visitor.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.sm.graduation.root.pojo.RootInfo;
import com.sm.graduation.root.service.RootInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sm.graduation.common.result.AjaxResult.*;

@SuppressWarnings("all")
@RestController
@RequestMapping("/root")
public class RootInfoController {

    @Autowired
    private RootInfoService rootInfoService;

    @Autowired
    private AdminInfoService adminInfoService;

    @Autowired
    private UsrInfoService usrInfoService;

    @Autowired
    private HealthRecordsService healthRecordsService;

    @Autowired
    private HighRiskService highRiskService;

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private MonthlyCateringService monthlyCateringService;

    @Autowired
    private GoOutService goOutService;

    @Autowired
    private OlderInfoService olderInfoService;

    @Autowired
    private DormitoryAllocationService dormitoryAllocationService;

    @Autowired
    private AccidentRecordService accidentRecordService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private NursingService nursingService;

    private String olderName;



    private boolean login;
    /** ???????????? */
    @RequestMapping("/loginIn")
    public AjaxResult loginIn(HttpServletRequest request, HttpSession session, String username,
                              LoginPojo loginPojo, RootInfo rootInfo,AdminInfo adminInfo, UsrInfo usrInfo){

        if (loginPojo.getCaptcha().equalsIgnoreCase(String.valueOf(request.getSession().getAttribute("captCode")))) {
            //????????????
            if (0 == loginPojo.getPower()){
                rootInfo.setName(loginPojo.getUsername());
                rootInfo.setPwd(loginPojo.getPassword());
                RootInfo root = rootInfoService.sltName(rootInfo);
                if (root == null){
                    return AjaxResult.error("??????????????????");
                }
                login = rootInfoService.loginIn(rootInfo);
                if (login) {
                    session.setAttribute("username",root.getNickName());
                    session.setAttribute("power",root.getPower());
                    session.setAttribute("root",root);
                    return AjaxResult.success(0,"????????????");
                } else return AjaxResult.error("????????????????????????");
            }

            //???????????????
            if (1 == loginPojo.getPower()){
                adminInfo.setAdminLogin(loginPojo.getUsername());
                adminInfo.setAdminPwd(loginPojo.getPassword());
                AdminInfo admin = adminInfoService.sltName(adminInfo);
                if (admin == null){
                    return AjaxResult.error("??????????????????");
                }
                login = adminInfoService.loginIn(adminInfo);
                if (login) {
                    session.setAttribute("username",admin.getAdminName());
                    session.setAttribute("power",admin.getPower());
                    session.setAttribute("admin",admin);
                    return AjaxResult.success(1,"????????????");
                } else return AjaxResult.error("????????????????????????");
            }

            //????????????
            if (2 == loginPojo.getPower()){
                usrInfo.setUsrLogin(loginPojo.getUsername());
                usrInfo.setUsrPwd(loginPojo.getPassword());
                UsrInfo usr = usrInfoService.sltName(usrInfo);
                if (usr == null){
                    return AjaxResult.error("??????????????????");
                }
                login = usrInfoService.loginIn(usrInfo);
                if (login) {
                    String olderName = usr.getOlderName();
                    HealthRecords older = healthRecordsService.older_dorm(olderName);
                    this.olderName = older.getName();
                    session.setAttribute("username",usr.getUsrName());
                    session.setAttribute("power",usr.getUsrPwd());
                    session.setAttribute("usr",usr);
                    session.setAttribute("older",older);
                    return AjaxResult.success(2,"????????????");
                } else return AjaxResult.error("????????????????????????");
            }

        }
            return AjaxResult.error("???????????????");

    }


    /** admin  ---  List */
    @RequestMapping("/adminList")
    public AjaxResult adminList(@RequestParam(defaultValue = "1" , value = "page") Integer pageNum,
                                @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                                @RequestParam(defaultValue = ""  , value = "adminName") String user
                                ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (user == null || user.equals("")) {
            map.put("user","");
            List<AdminInfo> admins = adminInfoService.listAll(map);
            PageInfo<AdminInfo> page = new PageInfo<>(admins);
            return successData(page.getTotal(), admins);
        }
            map.put("user",user);
            List<AdminInfo> admins = adminInfoService.listAll(map);
            PageInfo<AdminInfo> page = new PageInfo<>(admins);
            return successData(page.getTotal(), admins);
    }


    /** usr --- List */
    @RequestMapping("/userList")
    public AjaxResult userList(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                               @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                               @RequestParam(defaultValue = "" , value = "usrName") String usr
    ){

        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {

            map.put("usr","");
            List<UsrInfo> usrInfos = usrInfoService.listAll(map);

            PageInfo<UsrInfo> page = new PageInfo<>(usrInfos);

            return successData(page.getTotal(), usrInfos);
        }
        map.put("usr",usr);
        System.out.println(2222);
        List<UsrInfo> usrInfos = usrInfoService.listAll(map);
        for(String s:map.keySet()){
            System.out.println(s);
        }
        PageInfo<UsrInfo> page = new PageInfo<>(usrInfos);
        return successData(page.getTotal(), usrInfos);
    }


    /** ???????????? */
    @RequestMapping("/healthRisk")
    public AjaxResult healthRisk(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                                 @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                                 @RequestParam(defaultValue = "" , value = "olderName") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<HealthRecords> healthRecords = healthRecordsService.listAll(map);
            PageInfo<HealthRecords> page = new PageInfo<>(healthRecords);
            return successData(page.getTotal(), healthRecords);
        }
        map.put("usr",usr);
        List<HealthRecords> healthRecords = healthRecordsService.listAll(map);
        PageInfo<HealthRecords> page = new PageInfo<>(healthRecords);
        return successData(page.getTotal(), healthRecords);
    }


    /** ???????????? */
    @RequestMapping("/highRisk")
    public AjaxResult highRisk(@RequestParam(defaultValue = "1",  value = "page") Integer pageNum,
                               @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                               @RequestParam(defaultValue = ""  , value = "olderName") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<HighRisk> highRisks = highRiskService.listAll(map);
            PageInfo<HighRisk> page = new PageInfo<>(highRisks);
            return successData(page.getTotal(), highRisks);
        }
        map.put("usr",usr);
        List<HighRisk> highRisks = highRiskService.listAll(map);
        PageInfo<HighRisk> page = new PageInfo<>(highRisks);
        return successData(page.getTotal(), highRisks);
    }


    /** ???????????? */
    @RequestMapping("/medication")
    public AjaxResult medication(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                                 @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                                 @RequestParam(defaultValue = "" , value = "medication") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<Medication> medications = medicationService.listAll(map);
            PageInfo<Medication> page = new PageInfo<>(medications);
            return successData(page.getTotal(), medications);
        }
        map.put("usr",usr);
        List<Medication> medications = medicationService.listAll(map);
        PageInfo<Medication> page = new PageInfo<>(medications);
        return successData(page.getTotal(), medications);
    }


    /** ???????????? */
    @RequestMapping("/catering")
    public AjaxResult catering(@RequestParam(defaultValue = "1",  value = "page") Integer pageNum,
                               @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                               @RequestParam(defaultValue = ""  , value = "monTime") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<MonthlyCatering> monthlyCaterings = monthlyCateringService.listAll(map);
            PageInfo<MonthlyCatering> page = new PageInfo<>(monthlyCaterings);
            return successData(page.getTotal(), monthlyCaterings);
        }
        map.put("usr",usr);
        List<MonthlyCatering> monthlyCaterings = monthlyCateringService.listAll(map);
        PageInfo<MonthlyCatering> page = new PageInfo<>(monthlyCaterings);
        return successData(page.getTotal(), monthlyCaterings);
    }


    /** ???????????? */
    @RequestMapping("/goOut")
    public AjaxResult goOut(@RequestParam(defaultValue = "1",  value = "page") Integer pageNum,
                            @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                            @RequestParam(defaultValue = ""  , value = "olderName") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<GoOut> goOuts = goOutService.listAll(map);
            PageInfo<GoOut> page = new PageInfo<>(goOuts);
            return successData(page.getTotal(), goOuts);
        }
        map.put("usr",usr);
        List<GoOut> goOuts = goOutService.listAll(map);
        PageInfo<GoOut> page = new PageInfo<>(goOuts);
        return successData(page.getTotal(), goOuts);
    }

    @RequestMapping("/goOutUsr")
    public AjaxResult goOutUsr(@RequestParam(defaultValue = "1",  value = "page") Integer pageNum,
                            @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                            @RequestParam(defaultValue = ""  , value = "olderName") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("olderName",olderName);
            List<GoOut> goOuts = goOutService.go_listAll(map);
            PageInfo<GoOut> page = new PageInfo<>(goOuts);
            return successData(page.getTotal(), goOuts);
        }
        map.put("usr",usr);
        List<GoOut> goOuts = goOutService.listAll(map);
        PageInfo<GoOut> page = new PageInfo<>(goOuts);
        return successData(page.getTotal(), goOuts);
    }


    /** ???????????? */
    @RequestMapping("/register")
    public AjaxResult register(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                               @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                               @RequestParam(defaultValue = ""  , value = "olderName") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<OlderInfo> olderInfos = olderInfoService.listAll(map);
            PageInfo<OlderInfo> page = new PageInfo<>(olderInfos);
            return successData(page.getTotal(), olderInfos);
        }
        map.put("usr",usr);
        List<OlderInfo> olderInfos = olderInfoService.listAll(map);
        PageInfo<OlderInfo> page = new PageInfo<>(olderInfos);
        return successData(page.getTotal(), olderInfos);
    }


    /** ???????????? */
    @RequestMapping("/dormitory")
    public AjaxResult dormitory(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                                @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                                @RequestParam(defaultValue = ""  , value = "dormitory") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<DormitoryAllocation> dormitoryAllocations = dormitoryAllocationService.listAll(map);
            PageInfo<DormitoryAllocation> page = new PageInfo<>(dormitoryAllocations);
            return successData(page.getTotal(), dormitoryAllocations);
        }
        map.put("usr",usr);
        List<DormitoryAllocation> dormitoryAllocations = dormitoryAllocationService.listAll(map);
        PageInfo<DormitoryAllocation> page = new PageInfo<>(dormitoryAllocations);
        return successData(page.getTotal(), dormitoryAllocations);
    }

    /** ???????????? */
    @RequestMapping("/accident")
    public AjaxResult accident(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                               @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                               @RequestParam(defaultValue = ""  , value = "accTime") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("us","");
            List<AccidentRecord> accidentRecords = accidentRecordService.listAll(map);
            PageInfo<AccidentRecord> page = new PageInfo<>(accidentRecords);
            return successData(page.getTotal(), accidentRecords);
        }
        map.put("usr",usr);
        List<AccidentRecord> accidentRecords = accidentRecordService.listAll(map);
        PageInfo<AccidentRecord> page = new PageInfo<>(accidentRecords);
        return successData(page.getTotal(), accidentRecords);
    }

    /** ???????????? */
    @RequestMapping("/visitor")
    public AjaxResult visitor(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                              @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                              @RequestParam(defaultValue = ""  , value = "name") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<Visitor> visitors = visitorService.listAll(map);
            PageInfo<Visitor> page = new PageInfo<>(visitors);
            return successData(page.getTotal(), visitors);
        }
        map.put("usr",usr);
        List<Visitor> visitors = visitorService.listAll(map);
        PageInfo<Visitor> page = new PageInfo<>(visitors);
        return successData(page.getTotal(), visitors);
    }
    /** ???????????? */
    @RequestMapping("/checkIn")
    public AjaxResult checkIn(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                              @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                              @RequestParam(defaultValue = ""  , value = "year") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<CheckIn> checkIns = checkInService.listAll(map);
            PageInfo<CheckIn> page = new PageInfo<>(checkIns);
            return successData(page.getTotal(), checkIns);
        }
        map.put("usr",usr);
        List<CheckIn> checkIns = checkInService.listAll(map);
        PageInfo<CheckIn> page = new PageInfo<>(checkIns);
        return successData(page.getTotal(), checkIns);
    }
    /** ???????????? */
    @RequestMapping("/nursing")
    public AjaxResult nursing(@RequestParam(defaultValue = "1", value = "page") Integer pageNum,
                              @RequestParam(defaultValue = "10", value = "limit") Integer pageSize,
                              @RequestParam(defaultValue = ""  , value = "nurseRank") String usr
    ){
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (usr == null || usr.equals("")) {
            map.put("usr","");
            List<Nursing> nursings = nursingService.listAll(map);
            PageInfo<Nursing> page = new PageInfo<>(nursings);
            return successData(page.getTotal(), nursings);
        }
        map.put("usr",usr);
        List<Nursing> nursings = nursingService.listAll(map);
        PageInfo<Nursing> page = new PageInfo<>(nursings);
        return successData(page.getTotal(), nursings);
    }



    //==============????????????=================

    /** ??????????????? */
    @RequestMapping("/addAdmin")
    public AjaxResult addAmin(AdminInfo adminInfo){
        int insert = adminInfoService.insert(adminInfo);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }

    /** ???????????? */
    @RequestMapping("/addUsr")
    public AjaxResult addUsr(UsrInfo usrInfo){
        UsrInfo results = usrInfoService.sltName(usrInfo);
        if (results != null){
            return error("????????????,?????????????????????");
        }
        int insert = usrInfoService.insert(usrInfo);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }


    @RequestMapping("/addHealth")
    public AjaxResult addHealth(HealthRecords healthRecords){
        System.out.println(healthRecords);
        int insert = healthRecordsService.insert(healthRecords);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/addHigh")
    public AjaxResult addHigh(HighRisk highRisk){
        System.out.println(highRisk);
        int insert = highRiskService.insert(highRisk);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/addMedication")
    public AjaxResult addMedication(Medication medication){
        System.out.println(medication);
        int insert = medicationService.insert(medication);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }


    @RequestMapping("/addMon")
    public AjaxResult addMon(MonthlyCatering monthlyCatering){
        System.out.println(monthlyCatering);
        int insert = monthlyCateringService.insert(monthlyCatering);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }


    @RequestMapping("/addOut")
    public AjaxResult addOut(GoOut goOut){
        System.out.println(goOut);
        int insert = goOutService.insert(goOut);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }


    @RequestMapping("/addOlder")
    public AjaxResult addOlder(OlderInfo olderInfo){
        System.out.println(olderInfo);
        int insert = olderInfoService.insert(olderInfo);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }


    @RequestMapping("/addDorm")
    public AjaxResult addDorm(DormitoryAllocation dormitoryAllocation){
        System.out.println(dormitoryAllocation);
        int insert = dormitoryAllocationService.insert(dormitoryAllocation);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/addAcc")
    public AjaxResult addAcc(AccidentRecord accidentRecord){
        System.out.println(accidentRecord);
        int insert = accidentRecordService.insert(accidentRecord);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/addVis")
    public AjaxResult addVis(Visitor visitor){
        System.out.println(visitor);
        int insert = visitorService.insert(visitor);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/addCheck")
    public AjaxResult addCheck(CheckIn checkIn){
        System.out.println(checkIn);
        int insert = checkInService.insert(checkIn);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/addNurs")
    public AjaxResult addNurs(Nursing nursing){
        System.out.println(nursing);
        int insert = nursingService.insert(nursing);
        if (1 == insert){
            return success("????????????");
        }
        return error("????????????");
    }




    //==============????????????=================

    /** ??????????????? */
    @RequestMapping("/modifyAdmin")
    public AjaxResult modifyAmin(AdminInfo adminInfo){
        int update = adminInfoService.update(adminInfo);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyUsr")
    public AjaxResult modifyUsr(UsrInfo usrInfo){
        int update = usrInfoService.update(usrInfo);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyHealth")
    public AjaxResult modifyHealth(HealthRecords healthRecords){
        System.out.println(healthRecords);
        int update = healthRecordsService.update(healthRecords);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }


    @RequestMapping("/modifyHigh")
    public AjaxResult modifyHigh(HighRisk highRisk){
        System.out.println(highRisk);
        int update = highRiskService.update(highRisk);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }


    @RequestMapping("/modifyMedication")
    public AjaxResult modifyMedication(Medication medication){
        System.out.println(medication);
        int update = medicationService.update(medication);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyMon")
    public AjaxResult modifyMon(MonthlyCatering monthlyCatering){
        System.out.println(monthlyCatering);
        int update = monthlyCateringService.update(monthlyCatering);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyOut")
    public AjaxResult modifyOut(GoOut goOut){
        System.out.println(goOut);
        int update = goOutService.update(goOut);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyOlder")
    public AjaxResult modifyOlder(OlderInfo olderInfo){
        int update = olderInfoService.update(olderInfo);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyDorm")
    public AjaxResult modifyDorm(DormitoryAllocation dormitoryAllocation){
        int update = dormitoryAllocationService.update(dormitoryAllocation);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyAcc")
    public AjaxResult modifyAcc(AccidentRecord accidentRecord){
        int update = accidentRecordService.update(accidentRecord);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyVis")
    public AjaxResult modifyVis(Visitor visitor){
        int update = visitorService.update(visitor);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyCheck")
    public AjaxResult modifyCheck(CheckIn checkIn){
        int update = checkInService.update(checkIn);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }

    @RequestMapping("/modifyNurs")
    public AjaxResult modifyNurs(Nursing nursing){
        int update = nursingService.update(nursing);
        if (1 == update){
            return success("????????????");
        }
        return error("????????????");
    }




    //==============????????????=================


    /** ??????????????? */
    @RequestMapping("/delAdmin")
    public AjaxResult delAdmin(Integer adminId){
        adminInfoService.delAdmin(adminId);
        return success("????????????");
    }
    /** ????????????????????? */
    @RequestMapping("/batchDelAdmin")
    public AjaxResult batchDel(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    adminInfoService.delAdmin(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }

    /** ???????????? */
    @RequestMapping("/delUsr")
    public AjaxResult delUsr(Integer usrId){
        usrInfoService.delete(usrId);
        return success("????????????");
    }
    /** ?????????????????? */
    @RequestMapping("/batchDelUsr")
    public AjaxResult batchDelUsr(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    usrInfoService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }


    @RequestMapping("/delHealth")
    public AjaxResult delHealth(Integer id){
        healthRecordsService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelHealth")
    public AjaxResult batchDelHealth(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    healthRecordsService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }

    @RequestMapping("/delHigh")
    public AjaxResult delHigh(Integer id){
        highRiskService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelHigh")
    public AjaxResult batchDelHigh(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    highRiskService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }

    @RequestMapping("/delMedication")
    public AjaxResult delMedication(Integer id){
        medicationService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelMedication")
    public AjaxResult batchDelMedication(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    medicationService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }


    @RequestMapping("/delMon")
    public AjaxResult delMon(Integer id){
        monthlyCateringService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelMon")
    public AjaxResult batchDelMon(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    monthlyCateringService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }

    @RequestMapping("/delOut")
    public AjaxResult delOut(Integer id){
        goOutService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelOut")
    public AjaxResult batchDelOut(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    goOutService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }


    @RequestMapping("/delOlder")
    public AjaxResult delOlder(@RequestParam(value = "olderId") Integer id){
        olderInfoService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelOlder")
    public AjaxResult batchDelOlder(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    olderInfoService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }

    @RequestMapping("/delDorm")
    public AjaxResult delDorm(@RequestParam(value = "id") Integer id){
        dormitoryAllocationService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelDorm")
    public AjaxResult batchDelDorm(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    dormitoryAllocationService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }


    @RequestMapping("/delAcc")
    public AjaxResult delAcc(@RequestParam(value = "id") Integer id){
        accidentRecordService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelAcc")
    public AjaxResult batchDelAcc(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    accidentRecordService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }


    @RequestMapping("/delVis")
    public AjaxResult delVis(@RequestParam(value = "id") Integer id){
        visitorService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelVis")
    public AjaxResult batchDelVis(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    visitorService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }

    @RequestMapping("/delCheck")
    public AjaxResult delCheck(@RequestParam(value = "id") Integer id){
        checkInService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelCheck")
    public AjaxResult batchDelCheck(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    checkInService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }

    @RequestMapping("/delNurs")
    public AjaxResult delNurs(@RequestParam(value = "id") Integer id){
        nursingService.delete(id);
        return success("????????????");
    }

    @RequestMapping("/batchDelNurs")
    public AjaxResult batchDelNurs(String listStr){
        if (null != listStr && !"".equals(listStr)){
            String[] ids = listStr.split(",");
            for (String id:ids) {
                if (null != id && !"".equals(id)) {
                    System.out.println(id);
                    nursingService.delete(Integer.valueOf(id));
                }
            }
        }
        return success("????????????");
    }




    /** root ???????????? */
    @RequestMapping("/altPwd")
    public AjaxResult altPwd(String pwd,String rpwd) {
        if(!pwd.equals(rpwd)) return error( "?????????????????????");

        if (pwd != null && !pwd.equals("")) {
            int i = rootInfoService.altPwd(pwd);
            if (i != 0) {
                return success(0, "????????????");
            }
        }
        return error("?????????????????????");
    }

    @RequestMapping("/adminAltPwd")
    public AjaxResult adminAltPwd(String pwd,String rpwd,Map map,HttpServletRequest request) {
        if(!pwd.equals(rpwd)) return error( "?????????????????????");

        if (pwd != null && !pwd.equals("")) {
            AdminInfo admin= (AdminInfo) request.getSession().getAttribute("admin");
            Long id = admin.getAdminId();
            map.put("pwd",pwd);
            map.put("id",id);
            int i = adminInfoService.altPwd(map);
            if (i != 0) {
                return success(0, "????????????");
            }
        }
        return error("?????????????????????");
    }

    @RequestMapping("/usrAltPwd")
    public AjaxResult usrAltPwd(String pwd,String rpwd,Map map,HttpServletRequest request) {
        if(!pwd.equals(rpwd)) return error( "?????????????????????");

        if (pwd != null && !pwd.equals("")) {
            UsrInfo usr = (UsrInfo) request.getSession().getAttribute("usr");
            Long id = usr.getUsrId();
            map.put("pwd",pwd);
            map.put("id",id);
            int i = usrInfoService.altPwd(map);
            if (i != 0) {
                return success(0, "????????????");
            }
        }
        return error("?????????????????????");
    }
}
