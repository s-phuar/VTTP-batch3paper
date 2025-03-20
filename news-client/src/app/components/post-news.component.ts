import { AfterViewInit, Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { FileUpload } from '../fileupload.service';

@Component({
  selector: 'app-post-news',
  standalone: false,
  templateUrl: './post-news.component.html',
  styleUrl: './post-news.component.css'
})
export class PostNewsComponent implements OnInit, AfterViewInit{
  @ViewChild('fileUpload')
  imageFile !: ElementRef

  private fb = inject(FormBuilder)
  form !: FormGroup
  tagArray: string[] = []
  error: string = ''
  fileService = inject(FileUpload)


  ngOnInit(): void {
    this.createForm()
  }
  
  ngAfterViewInit() {
    // Now the fileUpload element is initialized, and you can access it safely
    console.log(this.imageFile); // This should work now
  }


  createForm(){
    this.form = this.fb.group({
      title: this.fb.control<string>('', [Validators.required, Validators.minLength(5)]),
      description: this.fb.control<string>('', [Validators.required, Validators.minLength(5)])
    })
  }


  addTag(input:HTMLInputElement){
    console.info('event...', input.value)
    const tag = input.value.trim()
    if(tag && !this.tagArray.includes(tag)){
      this.tagArray.push(tag)
      input.value = ''
    }
  }
  deleteTag(idx: number){
    this.tagArray.splice(idx, 1)
  }

  upload(){
    const file = this.imageFile.nativeElement.files[0]
    // console.info('file: ', file)
    // console.info('form: ', this.form.value)
    // console.info('tags: ', this.tagArray)

    if(!file){
      this.error = 'please upload image file'
      return
    }    
    //post to spring
    this.fileService.upload(this.form.value, file, this.tagArray)
  }

  validCheck(): boolean{
    const file = this.imageFile?.nativeElement?.files[0] || null; // Safe access
    return !(this.form.valid && !!file)
  }

  // New method to trigger change detection
  onFileChange() {
    // No-op, just triggers change detection
  }



}
