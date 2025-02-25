import { Link } from "react-router-dom";

const HomePage = () => {
  return (
    <div>
      <h1>Welcome to Lexislate</h1>
      <Link to="/login">Login</Link>
    </div>
  );
};

export default HomePage;
