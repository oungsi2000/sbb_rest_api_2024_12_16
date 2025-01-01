import { useContext, useEffect, useState } from 'react';
import { UserContext } from '../helper/getUserContext'
import { Navigate } from 'react-router-dom';


/**
 * @param {Array} role - 해당 페이지를 열람할 권한
 */

function Authorize(props) {
    const { user, setUser } = useContext(UserContext)
    const Component = props.component
    const role = props.role

    return (
        <>
            {role.includes(user.role) ? <Component /> :  <Navigate to="/login" replace={true}/>}
        </>
    )

}

export default Authorize