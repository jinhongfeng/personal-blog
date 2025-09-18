import { library } from '@fortawesome/fontawesome-svg-core'
import {
        faHome, faUser, faCog, faCircleNotch, faBullhorn, faEye, faEyeSlash, faHand, faExpand, faCompress,
        faSun, faCameraRetro, faStickyNote, faPencilSquare, faArrowUp, faAreaChart, faGlobe, faRandom,
        faDesktop, faFileText, faCamera, faNoteSticky, faComments, faHeartbeat, faCalendar, faCommenting,
        faEllipsisV, faUniversalAccess, faVolumeOff, faVolumeDown, faVolumeUp, faPlay, faQ, faPencil,
    faShoppingBag, faMusic, faImage, faAlignCenter, faAlignLeft, faAlignRight, faBook
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

// 注册图标到库中
library.add(faHome, faUser, faCog, faCircleNotch, faBullhorn, faEye, faEyeSlash, faHand,
    faSun, faCameraRetro, faStickyNote, faPencilSquare, faArrowUp, faAreaChart, faGlobe, faRandom,
    faDesktop, faFileText, faCamera, faComments, faNoteSticky, faHeartbeat, faCalendar, faCommenting,
    faEllipsisV, faUniversalAccess, faVolumeOff, faVolumeDown, faVolumeUp, faCompress,faExpand, faQ,
    faPlay, faPencil, faShoppingBag, faMusic, faImage, faAlignCenter, faAlignLeft, faAlignRight, faBook
    )

export { FontAwesomeIcon }
