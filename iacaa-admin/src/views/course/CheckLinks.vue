<template>
  <span style="">
   <el-form :inline="true" :model="serchForm" class="demo-form-inline" style="height: 50px">
      <el-form-item label="">
        <el-input v-model="serchForm.word" placeholder="课程名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getCourseList">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
      ref="multipleTable"
      :data="courses"
      style="width: 100%"
      height="750"
      tooltip-effect="dark"
    >
      <el-table-column
        type="selection"
        width="55"
      />
      <el-table-column
        type="index"
        label="序号"
        width="100"
      />
      <el-table-column
        prop="name"
        label="课程名称"
        width="200"
      />
        <el-table-column prop="courseTasks" type="expand" label="课程目标" width="1000">
        <template slot-scope="courseScope">
          <el-table :data="courseScope.row.courseTasks" stripe>
            <el-table-column
              prop="id"
              label="ID"
            />
            <el-table-column
              prop="describes"
              label="课程目标描述"
            />
            <el-table-column
              prop="createdDate"
              label="创建时间"
            />
            <el-table-column label="操作"
              prop="courseTasks">
              <template slot-scope="courseTaskScope">
                <el-button v-if="new Date(courseTaskScope.row.createdDate).getFullYear() === new Date().getFullYear()"
                           type="primary" icon="el-icon-edit" circle @click="handleCheckLinkEditForm(courseScope.row.name, courseTaskScope.row)" />
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      title="指标点支撑编辑"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="30%"
      center>
      <div>
        <el-form ref="ruleForm" :model="ckeckLinkEditForm" status-icon class="demo-ruleForm">
          <el-form-item label="课程" prop="name">
            <el-input v-model="ckeckLinkEditForm.courseName" disabled type="text" autocomplete="off" />
          </el-form-item>
          <el-form-item label="课程目标" prop="pass">
            <el-input v-model="ckeckLinkEditForm.courseTask.describes" disabled type="text" autocomplete="off" />
          </el-form-item>
          <el-form-item label="考核环节：" prop="pass">
            <el-button type="primary" round style="" @click="handleAddCheckLink">添加</el-button>
            <br>
            <el-table
              ref="multipleTable"
              style="width: 100%"
              height="50"
              tooltip-effect="dark">
            <el-table-column
              prop=""
              label="考核环节"
              width="250">
            </el-table-column>
            <el-table-column
              prop=""
              label="环节总分"
              width="90">
            </el-table-column>
            <el-table-column
              prop=""
              label="权重系数">
            </el-table-column>
          </el-table>
            <!--eslint-disable-next-line-->
            <span v-for="(item,index) in ckeckLinkEditForm.checkLinks" type="text" autocomplete="off">
              <el-select v-model="item.name" placeholder="标题" clearable filterable style="width: 50%;margin-top: 10px">
                <el-option label="期末考试" value="期末考试" />
                <el-option label="期中考试" value="期中考试" />
                <el-option label="日常作业" value="日常作业" />
                <el-option label="课堂表现" value="课堂表现" />
                <el-option label="日常考勤" value="日常考勤" />
              </el-select>
              <el-input v-model="item.targetScore"  label="目标分数" style="width: 10%;margin-top: 10px" />
              <el-input-number v-model="item.mix" :min="0.1" :max="1" step="0.1" label="权重系数" style="width: 30%;margin-top: 10px" />
              <el-button type="danger" icon="el-icon-delete" circle @click="deleteDiscribe(index)" style="margin-left: 10px"/>
            </span>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCheckLinksForm">确 定</el-button>
      </div>
    </el-dialog>
    <el-pagination
      :current-page="currentPage"
      :page-sizes="[10, 15, 20, 50, 100]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </span>

</template>

<script>
import {requestByClient, supplierConsumer} from "@/utils/HttpUtils";

export default {
  name: "CheckLinks",
  data(){
    return{
      dialogVisible: false,
      pageSize: 20,
      total: 0,
      currentPage: 1,
      tableData: [],
      courses: [],
      serchForm: {
        word: '',
        year: ''
      },
      ckeckLinkEditForm: {
        courseName: '',
        courseTask: {},
        checkLinks: []
      }
    }
  },
  mounted() {
    this.getCourseList()
  },
  methods:{
    getCourseList() {
      requestByClient(supplierConsumer, 'POST', 'course/voList', {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        word: this.serchForm.word
      }, res => {
        if (res.data.succ) {
          this.courses = res.data.data.list
          this.total = res.data.data.total
          this.pageSize = res.data.data.pageSize
          this.currentPage = res.data.data.pageNum
        }
      })
    },
    handleCheckLinkEditForm(courseName, courseTask) {
      this.dialogVisible = true
      this.ckeckLinkEditForm.courseTask = courseTask
      this.ckeckLinkEditForm.courseName = courseName
      requestByClient(supplierConsumer, 'POST', 'checkLink/list', {taskId: courseTask.id }, res => {
        if (res.data.succ) {
          this.ckeckLinkEditForm.checkLinks = res.data.data
        }
        this.loading = false
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.getCourseList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getCourseList()
    },
    deleteDiscribe(index){
      var check = this.ckeckLinkEditForm.checkLinks[index]
      if(check.id){
        requestByClient(supplierConsumer, 'POST','checkLink/delete', {
          id: check.id
        },res => {
          if (res.data.succ) {
            this.$message({
              message: '已删除',
              type: 'success'
            });
          }else {
            this.$message.error(res.data.msg);
          }
          this.loading = false
        })
      }
      this.ckeckLinkEditForm.checkLinks.splice(index,1)
    },
    handleAddCheckLink(){
      this.ckeckLinkEditForm.checkLinks.push({name:'',mix: '',targetScore: '',taskId: this.ckeckLinkEditForm.courseTask.id})
    },
    submitCheckLinksForm(){
      this.loading = true
      requestByClient(supplierConsumer, 'POST','checkLink/saveOrUpdate', this.ckeckLinkEditForm.checkLinks,res => {
        if (res.data.succ) {
          this.dialogVisible = false;
          this.$message({
            message: '修改成功',
            type: 'success'
          });
        }else {
          this.$message.error(res.data.msg);
        }
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>

</style>
