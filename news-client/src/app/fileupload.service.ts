import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { lastValueFrom } from "rxjs";

@Injectable()
export class FileUpload{

    private http = inject(HttpClient)

    upload(form: any, image: Blob, tags:string[]){
        const formData = new FormData()
        formData.set('title', form['title'])
        formData.set('description', form['description'])
        formData.set('file', image)
        formData.set('tags', JSON.stringify(tags))
        console.info('tags: ', JSON.stringify(tags))

        return lastValueFrom(this.http.post<any>('/api/upload', formData))
            .then(result => {
                console.info('postId: ', result)
                alert('newsId: ' + result.newsId)
            })
            .catch(err =>{
                console.error('something wrong ', err)
                alert('errorMSG: ' + err.error.fail)
            })


    }


}