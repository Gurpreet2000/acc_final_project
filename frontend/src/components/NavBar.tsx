import {
  NavigationMenu,
  NavigationMenuLink,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from '@/components/ui/navigation-menu';
import { Link } from 'react-router';

const NavBar = () => {
  return (
    <NavigationMenu className=" border-b-2 max-w-[100%] p-2 mb-2 justify-start">
      <NavigationMenuList>
        <Link to="/">
          <NavigationMenuLink
            className={navigationMenuTriggerStyle() + ' border-2'}
          >
            Home
          </NavigationMenuLink>
        </Link>
        <Link to="/search">
          <NavigationMenuLink
            className={navigationMenuTriggerStyle() + ' border-2'}
          >
            Search
          </NavigationMenuLink>
        </Link>
        <Link to="/history">
          <NavigationMenuLink
            className={navigationMenuTriggerStyle() + ' border-2'}
          >
            History
          </NavigationMenuLink>
        </Link>
      </NavigationMenuList>
    </NavigationMenu>
  );
};

export default NavBar;
