<template>
  <span>
  <el-form :inline="true" :model="serchForm" class="demo-form-inline" style="height: 50px">
    <el-form-item label="">
      <el-input v-model="serchForm.word" placeholder="名称" clearable></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="getList()">查询</el-button>
    </el-form-item>
  </el-form>
  <el-divider></el-divider>
  <el-table
    ref="multipleTable"
    :data="tableData"
    style="width: 100%"
    height="750"
    tooltip-effect="dark"
    @selection-change="handleSelectionChange">
    <el-table-column
      type="selection"
      width="55">
    </el-table-column>
    <el-table-column
      type="index"
      width="50">
    </el-table-column>
    <el-table-column
      prop="image"
      label="课程编号"
      width="100">
    </el-table-column>
    <el-table-column
      prop="name"
      label="名称"
      width="200">
    </el-table-column>
    <el-table-column
      prop="discribe"
      label="简介"
      width="900">
    </el-table-column>
    <el-table-column label="操作" >
      <template slot-scope="scope">
        <el-button type="primary" @click="handleEditForm(scope.row)">编辑课程目标</el-button>
      </template>
    </el-table-column>
  </el-table>
  <el-dialog
    title="课程目标编辑"
    :close-on-click-modal="false"
    :visible.sync="dialogVisible"
    width="75%"
    center>
    <div>
      <el-form :model="editForm" status-icon ref="ruleForm" class="demo-ruleForm">
        <el-form-item label="课程名称" prop="pass">
          <div style="font-size: 18px;color: #1a1a1a">{{editForm.name}}</div>
        </el-form-item>
        <el-form-item label="课程目标：" prop="pass">
          <el-button type="primary" round style="" @click="handleAddCourseTask">添加</el-button>
          <br>
          <el-table
            ref="multipleTable"
            style="width: 100%"
            height="50"
            tooltip-effect="dark">
            <el-table-column
              prop=""
              label="课程目标描述"
              width="770">
            </el-table-column>
            <el-table-column
              prop=""
              label="支撑指标点"
              width="490">
            </el-table-column>
            <el-table-column
              prop=""
              label="权重系数"
              width="100">
            </el-table-column>
          </el-table>
          <span v-for="(item,index) in editForm.courseTasks" type="text" autocomplete="off">
            <el-input type="text" autocomplete="off" v-model="item.describes" style="width: 55%;margin-top: 10px"></el-input>
            <el-select v-model="item.target.id" placeholder="可选支撑指标点" clearable style="width: 30%;margin-top: 10px">
              <el-option v-for="(item1,index1) in ableTarget" :key="index1" :label="item1.target.id + ':' + item1.target.discribe" :value="item1.target.id"></el-option>
            </el-select>
            <el-input-number v-model="item.mix" :min="0.1" :max="1" step="0.1" label="权重系数" style="width: 10%;margin-top: 10px"></el-input-number>
            <el-button type="danger" icon="el-icon-delete" circle @click="deleteDiscribe(index)"></el-button>
          </span>
        </el-form-item>
      </el-form>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitCourseTasks">确 定</el-button>
    </div>
  </el-dialog>
  <el-pagination
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :current-page="currentPage"
    :page-sizes="[10, 15, 20, 50, 100]"
    :page-size="pageSize"
    layout="total, sizes, prev, pager, next, jumper"
    :total="total">
  </el-pagination>
  </span>
</template>

<script>
import { requestByClient } from '@/utils/HttpUtils'
import { supplierConsumer } from '@/utils/HttpUtils'
export default {
  name: "Course",
  mounted() {
    this.getList()
  },
  data() {
    return {
      dialogVisible: false,
      visible: false,
      tableData: [],
      pageSize: 20,
      total : 0,
      currentPage: 1,
      ableTarget: [],
      serchForm: {
        word: ''
      },
      editForm: {
        id: '',
        name: '',
        courseTasks: [],
      }
    }
  },methods: {
    submitCourseTasks(){
      this.loading = true
      requestByClient(supplierConsumer, 'POST','courseTask/saveOrUpdate', this.editForm.courseTasks,res => {
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
    },
    getList() {
      this.loading = true
      requestByClient(supplierConsumer, 'POST','course/voList',{
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        word: this.serchForm.word
      },res => {
        if (res.data.succ) {
          this.tableData = res.data.data.list
          this.total = res.data.data.total
          this.pageSize = res.data.data.pageSize
          this.currentPage = res.data.data.pageNum
        }
        this.loading = false
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },
    handleDelete(){
      this.loading = true
      requestByClient(supplierConsumer, 'POST','course/del',{
          ids : this.ids}
        ,res => {
        if (res.data.succ) {
          this.$message({
            message: '删除成功',
            type: 'success'
          });
          this.getList()
        }
        this.loading = false
      })
    },
    handleSelectionChange(val) {
      const result = val.map(item => item.id)
      this.ids = result;
    },
    handleEditForm(row){
      this.editForm.id = row.id
      this.editForm.name = row.name
      requestByClient(supplierConsumer, 'POST','courseTask/voList',{
        courseId: row.id,
        year: new Date().getFullYear()
      },res => {
        if (res.data.succ) {
          this.editForm.courseTasks = res.data.data
        }
      })
      requestByClient(supplierConsumer, 'POST','courseTarget/voList',{
        courseId: row.id
      },res => {
        if (res.data.succ) {
          this.ableTarget = res.data.data
        }
      })
      this.dialogVisible = true
    },
    handleAddCourseTask() {
      this.editForm.courseTasks.push({describes:'',course:{id: this.editForm.id},target:{id:'' },mix: ''})
    },
    deleteDiscribe(index){
      var courseTask = this.editForm.courseTasks[index]
      if(courseTask.id){
        requestByClient(supplierConsumer, 'POST','courseTask/delete', {
          id: courseTask.id
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
      this.editForm.courseTasks.splice(index,1)
    },
  }
}
</script>

<style scoped>
.el-table__header tr,
.el-table__header th {
  padding: 0;
  height: 40px;
  line-height: 50px;
}
.el-table__body tr,
.el-table__body td {
  padding: 0;
  height: 40px;
  line-height: 30px;
}
.el-pagination{
  text-align: right;
}
.el-form{
  text-align: left;
}
.dialog-footer{
  margin-top: 0px;
}
.demo-form-inline{
  margin-left: 30px;
}
.el-main .el-divider--horizontal{
  margin: 0px 0;
}
</style>
