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
              width="200"
            />
            <el-table-column
              prop="describes"
              label="课程目标描述"
              width="700"
            />
            <el-table-column
              prop="createdDate"
              label="创建时间"
              width="200"
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
      title="课程目标成绩录入"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="30%"
      center>
      <div>
        <el-form ref="ruleForm" :model="ckeckLinkEditForm" status-icon class="demo-ruleForm">
          <el-form-item  prop="pass">
            <br>
            <el-table
              ref="multipleTable"
              style="width: 100%"
              height="50"
              tooltip-effect="dark">
            <el-table-column
              prop=""
              label="考核环节"
              width="240">
            </el-table-column>
            <el-table-column
              prop=""
              label="总分"
              width="150">
            </el-table-column>
            <el-table-column
              prop=""
              label="平均分">
            </el-table-column>
          </el-table>
            <!--eslint-disable-next-line-->
            <span v-for="(item,index) in ckeckLinkEditForm.checkLinks" type="text" autocomplete="off">
              <el-select v-model="item.name" placeholder="标题" disabled filterable style="width: 50%;margin-top: 10px">
                <el-option label="期末考试" value="期末考试" />
                <el-option label="期中考试" value="期中考试" />
                <el-option label="日常作业" value="日常作业" />
                <el-option label="课堂表现" value="课堂表现" />
                <el-option label="日常考勤" value="日常考勤" />
              </el-select>
              <el-input v-model="item.targetScore"  label="目标分数" style="width: 25%;margin-top: 10px" disabled/>
              <el-input v-model="item.averageScore"  label="平均分数" style="width: 25%;margin-top: 10px" @change="checkAvgScore(item.averageScore,item.targetScore,index)"/>
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
  name: "CheckLinkScore",
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
    checkAvgScore(avg,target,index){
      if(avg > target || avg < 0){
        this.$message({
          message: '平均成绩不得大于目标成绩且不小于零',
          type: 'error'
        });
        this.ckeckLinkEditForm.checkLinks[index].averageScore = ''
      }
    },
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
      let checkLinks = this.ckeckLinkEditForm.checkLinks
      for (let checkLink of checkLinks) {
        if(checkLink.averageScore === ''){
          this.$message({
            message: '平均成绩不得为空',
            type: 'error'
          });
          return false
        }
      }
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
