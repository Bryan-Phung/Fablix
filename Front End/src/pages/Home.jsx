import React from "react";
import styled from "styled-components";
import { FaBookmark,FaStar } from "react-icons/fa";
const StyledDiv = styled.div` 
`

const StyledH1 = styled.h1`
    color: --rich-black-forga-30;
`

const StyledSpan = styled.span`
    color: black;
    size: px;
`

const Home = () => {
    return (
        <div className="homeContainer">
            {/*<header class="navbar">*/}
            {/*    <button class="navbar-menu-btn">*/}
            {/*        <span>F</span><span>D</span>*/}
            {/*    </button>*/}

            {/*    <a href="/movie/search" class="navbar-brand">*/}
            {/*        <h1>Fablix</h1>*/}
            {/*    </a>*/}
            {/*</header>*/}

            <main>
                <h1>Fablix</h1>
                {/*<section className="banner">*/}
                {/*    <div className="banner-card">*/}
                {/*        <img src="https://cdn.wallpapersafari.com/66/24/cFnU7Y.png" className="banner-img"*/}
                {/*             alt=""/>*/}
                {/*        <div class="card-content">*/}
                {/*            <div class = "card-info">*/}
                {/*                <div class="genre">*/}
                {/*                    <ion-icon name ="film"/>*/}
                {/*                    <StyledSpan>Drama, Adventure</StyledSpan>*/}
                {/*                </div>*/}

                {/*            <div class ="year">*/}
                {/*                <ion-icon name ="calendar"/>*/}
                {/*                <StyledSpan>2019</StyledSpan>*/}
                {/*            </div>*/}

                {/*            <div className="quality">*/}
                {/*                 <ion-icon name="quality"/>*/}
                {/*                <StyledSpan>1080p</StyledSpan>*/}
                {/*            </div>*/}
                {/*        </div>*/}
                {/*            <StyledH1 class ="card-title">Re:Zero</StyledH1>*/}
                {/*        </div>*/}
                {/*    </div>*/}
                {/*</section>*/}

            {/*{this is for the movie banner layout}*/}
            {/*<section className = "movies">*/}
            {/*    <div className="filter-bar">*/}
            {/*        <div className={ "filter-dropdowns"}>*/}
            {/*            <select className= "genre">*/}
            {/*                <option value = "all genres">All genres</option>*/}
            {/*                <option value ="action">Action</option>*/}
            {/*                <option value ="adventure">Adventure</option>*/}
            {/*                <option value ="animal">Animal</option>*/}
            {/*                <option value ="animation">Animation</option>*/}
            {/*                <option value ="biography">Biography</option>*/}
            {/*            </select>*/}

            {/*            <select name= "year" className={"year"}>*/}
            {/*                <option value ="all years">All years</option>*/}
            {/*                <option value ="2022">2022</option>*/}
            {/*                <option value ="2020-2021">2020-2021</option>*/}
            {/*                <option value ="2010-2019">2010-2019</option>*/}
            {/*                <option value ="2000-2009">2000-2009</option>*/}
            {/*                <option value ="1980-1999">1980-1999</option>*/}
            {/*            </select>*/}

            {/*        </div>*/}

            {/*        <div className="filter-radios">*/}
            {/*            <input type="radio" name="grade" id="featured" checked/>*/}
            {/*                <label form="featured">Featured</label>*/}
            {/*            <input type="radio" name="grade" id="popular"/>*/}
            {/*               <label form="featured">Popular</label>*/}
            {/*            <input type="radio" name="grade" id="newest"/>*/}
            {/*                <label form="featured">Newest</label>*/}
            {/*        </div>*/}

            {/*    </div>*/}

            {/*    /!*{this is for the movie gridlayout}*!/*/}
            {/*    <div className="movies-grid">*/}
            {/*        <div className="movie-card">*/}
            {/*            <div className="card-head">*/}
            {/*                <img src="data:image/jpeg;base64,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" alt="Temporary img"*/}
            {/*                     className="card-img"/>*/}
            {/*                <div className="card-overlay">*/}
            {/*                    <div className="bookmark">*/}
            {/*                        <FaBookmark/>*/}
            {/*                    </div>*/}

            {/*                    <div className="rating">*/}
            {/*                        <FaStar/>*/}
            {/*                        <span>6.4</span>*/}
            {/*                    </div>*/}
            {/*                </div>*/}
            {/*                <div className="card-body">*/}
            {/*                    <h3 className="card-title">*/}
            {/*                        Red Notice*/}
            {/*                    </h3>*/}
            {/*                </div>*/}
            {/*                    <div className="card-info">*/}
            {/*                        <span className="genre">Action/Horror</span>*/}
            {/*                        <span className="year">2010</span>*/}
            {/*                    </div>*/}

            {/*            </div>*/}
            {/*        </div>*/}
            {/*    </div>*/}
            {/*</section>*/}




            </main>
        </div>
    );
}

export default Home;
