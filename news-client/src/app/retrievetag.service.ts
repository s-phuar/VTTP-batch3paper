import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable, Subject, tap } from "rxjs";
import { News, TagCount } from "./model";

@Injectable()
export class RetrieveTag{
    private http = inject(HttpClient)

    emitter = new Subject<TagCount[]>()
    emitter2 = new Subject<News[]>()

    duration : number = 5

    getDuration(): number{
        return this.duration
    }

    setDuration(dur: number){
        this.duration = dur
    }



    retrieve(duration: number): Observable<TagCount[]>{
        const params = new HttpParams() //duration converted to string internally
            .set('duration', duration)

        return this.http.get<TagCount[]>('/api/retrieve', {params})
            .pipe(
                tap(
                    (results: TagCount[]) => {
                        this.emitter.next(results)
                    }
                )
            )
    }

    retrieveNews(tag:string, duration:number): Observable<News[]>{
        const params = new HttpParams()
            .set('tag', tag)
            .set('duration', duration)

        return this.http.get<any>('/api/retrieveNews', {params})
            .pipe(
                tap(
                    (results:News[]) => {
                        this.emitter2.next(results)
                    }
                )
            )

    }


    


}