import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../helper/getUserContext';


function Header() {
    const { user, setUser } = useContext(UserContext)
   
    return (
        <nav className="navbar navbar-expand-lg navbar-light bg-light border-bottom">
            <div className="container-fluid">
                <a className="navbar-brand" href="/">SBB</a>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item">
                            {user.role === "ANONYMOUS" ? (<a className="nav-link" href="/login">로그인</a>) : (<a className="nav-link" href="/api/api/v1/logout">로그아웃</a>)}
                        </li>
                        <li className="nav-item">
                            {user.role === "ANONYMOUS" && <a className="nav-link" href="/oauth2/oauth2/authorization/google">구글로 로그인</a>}
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="/signup">회원가입</a>
                        </li>
                        <li className="nav-item">
                            {user.role === "USER" && <a className="nav-link" href="/my-page">마이페이지</a>}
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    );
}
export default Header;