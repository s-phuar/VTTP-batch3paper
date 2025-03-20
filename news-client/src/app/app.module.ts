import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PostNewsComponent } from './components/post-news.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { FileUpload } from './fileupload.service';
import { provideHttpClient } from '@angular/common/http';
import { LandingPageComponent } from './components/landing-page.component';
import { RetrieveTag } from './retrievetag.service';
import { DetailsComponent } from './components/details.component';

const appRoutes:Routes =[
  {path: '', component: LandingPageComponent},
  {path:'post', component: PostNewsComponent},
  {path:'details/:tag/:minutes', component: DetailsComponent},
  {path:'**', redirectTo: '/', pathMatch:'full'}
]

@NgModule({
  declarations: [
    AppComponent,
    PostNewsComponent,
    LandingPageComponent,
    DetailsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [FileUpload, provideHttpClient(), RetrieveTag],
  bootstrap: [AppComponent]
})
export class AppModule { }
