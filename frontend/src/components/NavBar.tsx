import {
  NavigationMenu,
  NavigationMenuLink,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from '@/components/ui/navigation-menu';
import { Link } from 'react-router';

const NavBar = () => {
  return (
    <NavigationMenu className="   max-w-[100%] p-2 mb-2 justify-start scroll">
      <NavigationMenuList>
        <Link to="/">
          <NavigationMenuLink className={navigationMenuTriggerStyle()}>
            Home
          </NavigationMenuLink>
        </Link>
        <Link to="/search">
          <NavigationMenuLink className={navigationMenuTriggerStyle()}>
            Search
          </NavigationMenuLink>
        </Link>
        <Link to="/search_history">
          <NavigationMenuLink className={navigationMenuTriggerStyle()}>
            Search History
          </NavigationMenuLink>
        </Link>
        <Link to="/frequency">
          <NavigationMenuLink className={navigationMenuTriggerStyle()}>
            Frequency Count
          </NavigationMenuLink>
        </Link>
      </NavigationMenuList>
    </NavigationMenu>
  );
};

export default NavBar;
